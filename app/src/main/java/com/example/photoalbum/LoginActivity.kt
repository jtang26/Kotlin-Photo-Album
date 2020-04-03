package com.example.photoalbum


   import androidx.appcompat.app.AppCompatActivity
   import android.content.Intent
   import android.os.Bundle
    import android.widget.Button
    import android.widget.TextView
    import android.widget.Toast
    import com.google.android.gms.tasks.OnCompleteListener
    import com.google.firebase.auth.FirebaseAuth
    import kotlinx.android.synthetic.main.login_layout.*
    import kotlinx.android.synthetic.main.signup.*

    class LoginActivity: AppCompatActivity() {
        public lateinit var emailText : TextView
        public lateinit var passText : TextView
        public lateinit var login: Button
        public lateinit var createUser: Button
        private lateinit var auth: FirebaseAuth
        public lateinit var resetButton: Button


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.login_layout)
            auth = FirebaseAuth.getInstance()
            login = findViewById(R.id.loginButton)
            createUser = findViewById(R.id.signupButton)
            loginButton.setOnClickListener() {
                var email = loginEmailBox.text.toString()
                var password = loginPasswordBox.text.toString()
                //Source cited for firebase login and create user: https://blog.mindorks.com/firebase-login-and-authentication-android-tutorial
                //Source cited: https://gist.github.com/mishra3452/1dda2f91840a9b349dd79c7c4d05b1f0
                if (email != "" && password != "") {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                            }
                        })
                } else {
                    Toast.makeText(this, "Email and Password Fields cannot be empty", Toast.LENGTH_LONG)
                        .show()
                }
            }
            createUser.setOnClickListener() {
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

