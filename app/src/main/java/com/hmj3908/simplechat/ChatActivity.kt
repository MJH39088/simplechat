package com.hmj3908.simplechat

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hmj3908.DataModel.ChatMessage
import com.hmj3908.ViewModel.ChatViewModel
import com.hmj3908.simplechat.databinding.ActivityChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private val viewModel: ChatViewModel by viewModels()
    private var _binding: ActivityChatBinding? = null
    protected val binding get() = _binding!!

    private var socket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private val clientSockets = mutableListOf<Socket>()
    private val messages = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messages)
        binding.chatList.adapter = adapter

        val isServer = intent.getStringExtra("ipAddress") == null
        if (isServer) startServer() else connectToServer(intent.getStringExtra("ipAddress")!!)

        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (message.isNotBlank()) sendMessage(message)
        }

        binding.exitButton.setOnClickListener {
            if (isServer) stopServer() else disconnectFromServer()
            finish()
        }
    }

    private fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in interfaces) {
                if (!networkInterface.isUp || networkInterface.isLoopback) continue
                val addresses = networkInterface.inetAddresses
                for (address in addresses) {
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun startServer() {
        Thread {
            try {
                serverSocket = ServerSocket(12345)
                val serverIp = getLocalIpAddress()
                runOnUiThread {
                    binding.serverInfoText.text = if (serverIp != null) {
                        "서버 IP: $serverIp\n포트: 12345"
                    } else {
                        "서버 IP를 가져올 수 없습니다."
                    }
                }
                while (!serverSocket!!.isClosed) {
                    val clientSocket = serverSocket!!.accept()
                    clientSockets.add(clientSocket)
                    listenForMessages(clientSocket)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun connectToServer(ipAddress: String) {
        Thread {
            try {
                socket = Socket(ipAddress, 12345)
                runOnUiThread { Toast.makeText(this, "서버에 연결됨", Toast.LENGTH_SHORT).show() }
                listenForMessages(socket!!)
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { Toast.makeText(this, "서버 연결 실패", Toast.LENGTH_SHORT).show() }
            }
        }.start()
    }

    private fun listenForMessages(socket: Socket) {
        Thread {
            try {
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                var message: String?
                while (true) {
                    message = input.readLine()
                    if (message == null) break
                    runOnUiThread {
                        messages.add(message)
                        adapter.notifyDataSetChanged()

                        // 상대방이 disconnect 메시지를 보낸 경우 처리
                        if (message == "disconnect") {
                            showDisconnectDialog()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun sendMessage(message: String) {
        Thread {
            try {
                if (socket != null) {
                    PrintWriter(socket!!.getOutputStream(), true).println(message)
                } else {
                    clientSockets.forEach { client ->
                        PrintWriter(client.getOutputStream(), true).println(message)
                    }
                }
                runOnUiThread {
                    messages.add("Me: $message")
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun disconnectFromServer() {
        Thread {
            try {
                socket?.getOutputStream()?.write("disconnect\n".toByteArray())
                socket?.getOutputStream()?.flush()
                socket?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun stopServer() {
        Thread {
            try {
                clientSockets.forEach { client ->
                    client.getOutputStream().write("disconnect\n".toByteArray())
                    client.close()
                }
                serverSocket?.close()
                runOnUiThread { Toast.makeText(this, "서버 종료됨", Toast.LENGTH_SHORT).show() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun showDisconnectDialog() {
        runOnUiThread {
            AlertDialog.Builder(this).apply {
                setTitle("연결 종료")
                setMessage("상대방이 연결을 종료했습니다.")
                setPositiveButton("확인") { _, _ ->
                    val intent = Intent(this@ChatActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                setCancelable(false)
                show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectFromServer()
        stopServer()
        _binding = null
    }
}
