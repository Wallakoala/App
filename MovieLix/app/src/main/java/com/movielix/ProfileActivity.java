package com.movielix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.movielix.firestore.FirestoreConnector;
import com.movielix.interfaces.IDeleteListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        // Establecemos el nombre y el email de la cabecera
        TextView name  = findViewById(R.id.profile_name);
        TextView email = findViewById(R.id.profile_email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        name.setText(Objects.requireNonNull(user).getDisplayName());
        email.setText(user.getEmail());

        CircleImageView profilePic = findViewById(R.id.profile_pic);
        Picasso.get()
                .load(user.getPhotoUrl())
                .error(R.drawable.ic_default_profile_pic)
                .into(profilePic);

        // Sign out
        findViewById(R.id.profile_sign_out).setOnClickListener(v -> new AlertDialog.Builder(this, R.style.MyAlertDialogStyleLight)
                .setTitle("Salir")
                .setMessage("¿Seguro que quieres salir de tu cuenta?")
                .setPositiveButton("Salir", (dialog, whichButton) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(this, IntroActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null).show());

        // Delete account
        findViewById(R.id.profile_delete).setOnClickListener(v -> new AlertDialog.Builder(this, R.style.MyAlertDialogStyleLight)
                .setTitle("Borrar cuenta")
                .setMessage("¿Seguro que quieres borrar tu cuenta? Perderás toda tu información asociada a tu cuenta.\n\nEsta operación es irreversible.")
                .setPositiveButton("Borrar", (dialog, whichButton) -> {
                    FirestoreConnector.newInstance().deleteUser(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), new IDeleteListener() {
                        @Override
                        public void onSuccess() {
                            FirebaseAuth.getInstance().getCurrentUser().delete();
                            Intent intent = new Intent(ProfileActivity.this, IntroActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError() {
                            View container = findViewById(R.id.profile_container);
                            Snackbar.make(container, R.string.something_went_wrong, Snackbar.LENGTH_SHORT).show();
                        }
                    });

                })
                .setNegativeButton("Cancelar", null).show());
    }
}
