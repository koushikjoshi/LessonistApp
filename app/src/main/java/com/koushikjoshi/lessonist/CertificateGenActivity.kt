package com.koushikjoshi.lessonist

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.bold
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.*


class CertificateGenActivity : AppCompatActivity() {

    lateinit var textView8: TextView
    lateinit var textView9: TextView
    lateinit var textView10: TextView
    lateinit var downloadButton: Button
    lateinit var shareButton: Button
    lateinit var clayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certificate_gen)

        textView8 = findViewById(R.id.textView8)
        textView9 = findViewById(R.id.textView9)
        textView10 = findViewById(R.id.textView10)

        downloadButton = findViewById(R.id.button4)
        shareButton = findViewById(R.id.button5)

        clayout = findViewById(R.id.certificateLayout)

        var courseName = intent.extras?.get("courseName")
        val user = FirebaseAuth.getInstance().currentUser
        var name = user?.displayName.toString()
        var nameFinal = name.trim().split("\\s+".toRegex()).map { it.capitalize() }.joinToString(" ")

        var string1 = "This is to certify that "
        var string2 = " has successfully, as per their own assessment, completed the course titled "

        var finalString = SpannableStringBuilder().append(string1).bold { append(nameFinal) }.append(string2).bold { append(courseName.toString()) }

        textView8.setText(finalString)

        var db = Firebase.database
        var ref = db.getReference("courses")



        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    if(it.child("name").value.toString()==courseName.toString()){
                        var channel_name = it.child("channel_name").value.toString()
                        var channel_id = it.child("channel_id").value.toString()
                        var stringfortv9 = SpannableStringBuilder().append("All credit for the creation of this course belongs to ").bold { append(channel_name) }.append(" on YouTube")
                        textView9.setText(stringfortv9)
                        textView10.setText("/"+channel_id)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



        downloadButton.setOnClickListener {

            var image = getBitMapFromView(clayout)
            val rand_no = (100000..999999).random()

//            if (image != null) {
//                saveFile(this, image, nameFinal.replace("\\s".toRegex(), "")+"_Certificate_"+rand_no.toString())
//            }

//            if (image != null) {
//                cacheToLocal(nameFinal.replace("\\s".toRegex(), "")+"_Certificate_"+rand_no.toString(), image)
//            }

            if (image != null) {
                saveMediaToStorage(image, this, nameFinal.replace("\\s".toRegex(), "")+"_Certificate_"+rand_no.toString())
            }

        }

        shareButton.setOnClickListener {
            var image = getBitMapFromView(clayout)

            val rand_no = (100000..999999).random()

            var bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), image,nameFinal.replace("\\s".toRegex(), "")+"_Certificate_"+rand_no.toString()+"_"+"${System.currentTimeMillis()}.jpg", "Check out my course completion certificate from the Lessonist App");
            var bitmapUri = Uri.parse(bitmapPath);
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri );
            intent.setType("image/png");
            startActivity(intent);
        }


        }



    fun getBitMapFromView(view: View): Bitmap? {

        //Define a bitmap with the same size as the view
        val returnedBitmap =
            Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable: Drawable = view.getBackground()
        if (bgDrawable != null) //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas) else  //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    fun saveMediaToStorage(bitmap: Bitmap, context: Context, name: String) {
        //Generating a file name
        val filename = name+"_"+"${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            context?.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Certificate saved in your gallery", Toast.LENGTH_SHORT).show()
        }
    }



}