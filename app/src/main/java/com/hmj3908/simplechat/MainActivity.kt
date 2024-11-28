package com.hmj3908.simplechat

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hmj3908.DataModel.ChatRoom
import com.hmj3908.ViewModel.MainViewModel
import com.hmj3908.simplechat.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var _binding: ActivityMainBinding? = null
    protected val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 방 생성 버튼
        binding.createRoomButton.setOnClickListener {
            if (isNetworkAvailable()) {
                val roomName = binding.roomNameInput.text.toString()
                if (roomName.isNotBlank()) {
                    viewModel.createRoom(roomName) { createdRoomId ->
                        val intent = Intent(this, ChatActivity::class.java).apply {
                            putExtra("roomid", createdRoomId)
                        }
                        startActivity(intent)
                    }
                    Toast.makeText(this, "방 생성 요청: $roomName", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "방 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "네트워크에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 서버 연결 버튼
        binding.connectButton.setOnClickListener {
            showConnectDialog()
        }

        // 방 삭제 (롱클릭)
        binding.roomsList.setOnItemLongClickListener { _, _, position, _ ->
            val room = viewModel.rooms.value?.get(position)
            if (room != null) {
                showDeleteRoomDialog(room)
            }
            true
        }

        observeRooms()
    }

    private fun showDeleteRoomDialog(room: ChatRoom) {
        AlertDialog.Builder(this).apply {
            setTitle("방 삭제")
            setMessage("정말로 ${room.roomName} 방을 삭제하시겠습니까?")
            setPositiveButton("확인") { _, _ -> viewModel.deleteRoom(room) }
            setNegativeButton("취소", null)
            show()
        }
    }

    private fun showConnectDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("IP 주소 입력")
        val input = EditText(this)
        input.hint = "e.g., 192.168.1.100"
        dialog.setView(input)

        dialog.setPositiveButton("Connect") { _, _ ->
            val ipAddress = input.text.toString()
            if (ipAddress.isNotBlank()) {
                connectToServer(ipAddress)
            } else {
                Toast.makeText(this, "IP 주소를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
        dialog.show()
    }

    private fun connectToServer(ipAddress: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("ipAddress", ipAddress) // 서버 IP 전달
        startActivity(intent)
    }

    private fun observeRooms() {
        viewModel.rooms.observe(this) { rooms ->
            val roomNames = rooms.map { it.roomName }
            binding.roomsList.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                roomNames
            )
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
