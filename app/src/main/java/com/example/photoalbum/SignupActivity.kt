package com.example.photoalbum

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.login_layout.*
import kotlinx.android.synthetic.main.signup.*

class SignupActivity : AppCompatActivity() {
    public lateinit var emailText : TextView
    public lateinit var passText : TextView
    public lateinit var userText : TextView
    public lateinit var signButton: Button
    public lateinit var loginButton1: Button
    public lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        emailText = findViewById(R.id.emailBox)
        passText = findViewById(R.id.passwordBox)

        // Danny added
        userText = findViewById(R.id.usernameBox)
        //

        signButton = findViewById(R.id.createButton)
        loginButton1 = findViewById(R.id.loginButton1)
        createButton.setOnClickListener() {
            var email = emailText.text.toString()
            var password = passText.text.toString()
            var username = userText.text.toString()
            if (email != "" && password != "") {
                //Source cited for firebase login and create user: https://blog.mindorks.com/firebase-login-and-authentication-android-tutorial
                //Source cited: https://gist.github.com/mishra3452/8eb61e899afe0700ed1fffe6f4353b4c
                auth?.createUserWithEmailAndPassword(email, password)!!
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG)
                                .show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("username", username)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Registration Failed User already exists!", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Email and Password Fields cannot be empty", Toast.LENGTH_LONG)
                    .show()
            }
        }
        loginButton1.setOnClickListener() {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}