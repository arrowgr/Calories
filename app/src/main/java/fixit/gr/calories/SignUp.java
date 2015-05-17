package fixit.gr.calories;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by marios on 13/05/2015.
 */
public class SignUp extends Activity {


    private EditText name ;
    private EditText email ;
    private EditText pass1;
    private EditText pass2 ;
    private Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.sign_up);

        name = (EditText) findViewById(R.id.reg_fullname);
        email = (EditText) findViewById(R.id.reg_email);
        pass1 = (EditText) findViewById(R.id.reg_password);
        pass2 = (EditText) findViewById(R.id.reg_password_again);
        register = (Button) findViewById(R.id.btnRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(pass1.getText()).equals(pass2.getText())){
                    Toast.makeText(getApplicationContext(), "The passwords do not match Try again .", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Succesful Sing Up"+"\nName :"+name.getText()+"\nEmail :"+email.getText()+"\nPassword :"+pass1.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
