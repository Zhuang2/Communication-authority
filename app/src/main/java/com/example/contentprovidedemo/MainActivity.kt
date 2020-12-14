package com.example.contentprovidedemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val list = ArrayList<String>()
    private lateinit var adapter:ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,list)
        CallListView.adapter = adapter
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS),2)
        }
        else readContacts()

        CallButton.setOnClickListener {
                                //检查权限授予状态         上下文               申请什么权限                 授权状态.已授权
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                                //申请权限                上下文       申请权限的数组集合                       请求代码
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE),1)
            }
            else call()
        }
    }

    private fun readContacts() {
        val contentResolver = contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        contentResolver.query(uri,null,null,null,null)?.apply {
            while (moveToNext()){
                val name = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                list.add("$name $number")
            }
            adapter.notifyDataSetChanged()
            close()
        }
    }

    //申请权限返回值
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1->{
                if (grantResults.isNotEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    call()
                }else{
                    Toast.makeText(this,"拨打电话功能需要使用您授予电话使用权限，否则我们无法为您提供这项功能",Toast.LENGTH_LONG).show()
                }
            }
            2->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    readContacts()
                }  else{
                    Toast.makeText(this,"获取通讯录功能需要您授权通讯录权限，否则我们无法为您提供这项功能",Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun call() {
        val intent = Intent(Intent.ACTION_CALL)
//             val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:10010")
        startActivity(intent)
    }
}