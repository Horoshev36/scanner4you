package com.google.android.gms.samples.vision.scanner4you;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

    public class activity_login extends Activity {

        // Объявляем об использовании следующих объектов:
        private EditText username;
        private EditText password;
        private Button login;
        private TextView loginLocked;
        private TextView attempts;
        private TextView numberOfAttempts;

        // Число для подсчета попыток залогиниться:
        int numberOfRemainingLoginAttempts = 3;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            // Связываемся с элементами нашего интерфейса:
            username = (EditText) findViewById(R.id.edit_user);
            password = (EditText) findViewById(R.id.edit_password);
            login = (Button) findViewById(R.id.button_login);
            loginLocked = (TextView) findViewById(R.id.login_locked);
            attempts = (TextView) findViewById(R.id.attempts);
            numberOfAttempts = (TextView) findViewById(R.id.number_of_attempts);
            //numberOfAttempts.setText(Integer.toString(numberOfRemainingLoginAttempts));

        }

        // Обрабатываем нажатие кнопки "Войти":
        public void Login(View view) {

            // Если введенные логин и пароль будут словом "admin",
            // показываем Toast сообщение об успешном входе:
            if (username.getText().toString().equals("admin") &&
                    password.getText().toString().equals("admin")) {
                Toast.makeText(getApplicationContext(), "Вход выполнен!",Toast.LENGTH_SHORT).show();

                // Выполняем переход на другой экран:
                Intent intent = new Intent(activity_login.this,MainActivity.class);
                startActivity(intent);

            }

            // В другом случае выдаем сообщение с ошибкой:
            else {
                Toast.makeText(getApplicationContext(), "Неправильные данные!",Toast.LENGTH_SHORT).show();
               // numberOfRemainingLoginAttempts--;

                // Делаем видимыми текстовые поля, указывающие на количество оставшихся попыток:
               // attempts.setVisibility(View.VISIBLE);
               // numberOfAttempts.setVisibility(View.VISIBLE);
               // numberOfAttempts.setText(Integer.toString(numberOfRemainingLoginAttempts));

                // Когда выполнено 3 безуспешных попытки залогиниться,
                // делаем видимым текстовое поле с надписью, что все пропало и выставляем
                // кнопке настройку невозможности нажатия setEnabled(false):
                // if (numberOfRemainingLoginAttempts == 0) {
                //     login.setEnabled(false);
                //     loginLocked.setVisibility(View.VISIBLE);
                //     loginLocked.setBackgroundColor(Color.RED);
                //     loginLocked.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                //     loginLocked.setText("Вход заблокирован!!!");
                // }
            }
        }
    }

