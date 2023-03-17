package apps.hadjou.ilyas.sunhopeapp.Fragment


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import apps.hadjou.ilyas.sunhopeapp.ChngerPassword
import apps.hadjou.ilyas.sunhopeapp.Glade.GlideApp
import apps.hadjou.ilyas.sunhopeapp.Modul.User
import apps.hadjou.ilyas.sunhopeapp.R
import apps.hadjou.ilyas.sunhopeapp.SignIn
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_edit.*
import java.io.ByteArrayOutputStream
import java.util.*

class EditFragment : Fragment() ,TextWatcher {
    override fun afterTextChanged(p0: Editable?) {}
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        enregistrer.isEnabled=
            Cnew_password.text.trim().isNotEmpty()

    }

    //declaration
    companion object {
        val RC_SELECT_TMAGE = 2
    }

    // Fire Base
    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance() //princpale root of storage
    }
    private val fireStorInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance() //root of auth
    }
    private val userDocRef: DocumentReference
        get()=fireStorInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    private val userStorageRef: StorageReference // in root born child with name "uid"
        get() = storageInstance.reference.child(FirebaseAuth.getInstance().currentUser!!.uid)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Get the custom view for this fragment layout
        val view = inflater.inflate(R.layout.fragment_edit, container, false)

        val change_password = view.findViewById<Button>(R.id.change_password)
        change_password.setOnClickListener{
           val goToChangePassword=Intent(activity, ChngerPassword::class.java)
            startActivity(goToChangePassword)

        }

        getUserInfo { user ->
            if (user.imageProfile.isNotEmpty()) {
                GlideApp.with(activity!!).load(storageInstance.getReference(user.imageProfile))
                    .placeholder(R.drawable.ic_account_circle_blue) // if has not image set défault
                    .into(image_profil)
            }
            if(user.name.isNotEmpty()){
                nameTextView.text=user.name
                prenom.setHint(user.name)
            }
            if(user.email.isNotEmpty()){
                email.setHint(user.email)
            }
            if(user.familyName.isNotEmpty()){
                family_name.setHint(user.familyName)
            }
        }

        // Get the image view widget reference from custom layout
        val tv = view.findViewById<ImageView>(R.id.image_profil)

        // Set a click listener for image profile
        tv.setOnClickListener {

            val myIntentImage= Intent().apply {
                type="image/*"
                action=Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
            }
            startActivityForResult(Intent.createChooser(myIntentImage,"Select Image Profile"), RC_SELECT_TMAGE)
        }

        // Log Out event
        val deconctionButton = view.findViewById<Button>(R.id.signout)
        deconctionButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val gotoSigninView =Intent(activity,SignIn::class.java)
            startActivity(gotoSigninView)
        }

        val save = view.findViewById<Button>(R.id.enregistrer)
        save.setOnClickListener{
            enregistrer()

        }

        val vide = view.findViewById<EditText>(R.id.Cnew_password)
       vide.addTextChangedListener(this)

        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SELECT_TMAGE && resultCode == Activity.RESULT_OK && data !=null && data.data!=null){

            //put the image into image profile
            image_profil.setImageURI(data.data)
            progress_barr.visibility=View.VISIBLE


            val imagePath=data.data
            val imageBmp =MediaStore.Images.Media.getBitmap(activity!!.contentResolver,imagePath)
            val ouPutStream = ByteArrayOutputStream()

            // compressed the image and transfer it to Byte value
            imageBmp.compress(Bitmap.CompressFormat.JPEG,20,ouPutStream)
            val CompressImageByte =ouPutStream.toByteArray()

            // apply fun of upload image with high order function to put image path in fire store data base
            uploadProfileImage(CompressImageByte){path ->
                val userFieldMap = mutableMapOf<String,Any>()
                    userFieldMap["imageProfile"]=path
                    userDocRef.update(userFieldMap)
                    progress_barr.visibility=View.GONE
            }
        }
    }




    private fun enregistrer () {

        val familyName=family_name.text.toString().trim()
        val name=prenom.text.toString().trim()
        val e_mail=email.text.toString().trim()
        val old_pass_word=Cnew_password.text.toString().trim()
        val new_pass_word =""
        //val new_pass_word =new_password.text.toString().trim()
        family_name.setText("")
        prenom.setText("")
        email.setText("")
        Cnew_password.setText("")
        //new_password.setText("")

        //check password for make changer

        userDocRef.get().addOnSuccessListener { document ->
            val data = document.get("password").toString()
            val original_email = document.get("email").toString()

            if (data == old_pass_word && old_pass_word.isNotEmpty()) {

                // update data base

                    if (name.isNotEmpty())userDocRef.update("name", name)
                    if (familyName.isNotEmpty())userDocRef.update("familyName", familyName)


            // [START reauthenticate
            val user = FirebaseAuth.getInstance().currentUser
            val credential = EmailAuthProvider
                .getCredential(original_email,old_pass_word)
            // Prompt the user to re-provide their sign-in credentials
            user?.reauthenticate(credential)
                ?.addOnCompleteListener {
                    if(e_mail.isNotEmpty()) {
                        user?.updateEmail(e_mail).addOnSuccessListener {
                            Toast.makeText(activity, "Email a été mis à jour", Toast.LENGTH_SHORT).show()
                            if (e_mail.isNotEmpty())userDocRef.update("email", e_mail)
                            FirebaseAuth.getInstance().signOut()
                            val gotoSigninView =Intent(activity,SignIn::class.java)
                            startActivity(gotoSigninView)
                        }.addOnFailureListener {
                            Toast.makeText(activity, "Error ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    if(new_pass_word.isNotEmpty()){
                        user?.updatePassword(new_pass_word).addOnSuccessListener {
                            Toast.makeText(activity,"mot de pass a été mis à jour",Toast.LENGTH_LONG).show()
                           userDocRef.update("password", new_pass_word)
                        }.addOnFailureListener {
                            Toast.makeText(activity, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                     }
            // [END reauthenticate]

            } else {
                Toast.makeText(activity, "L'ancien mot de passe est incorrect \n", Toast.LENGTH_SHORT).show()
            }
        }

        // delete text after click save


    }



    private fun confermation(usermdp : String):Boolean{
        var temp:Boolean=false
       userDocRef.get().addOnSuccessListener { document ->
            val data = document.get("password").toString()
            if (data != usermdp) {
                Toast.makeText(activity,"False ${data}== ${usermdp}", Toast.LENGTH_SHORT).show()
            }
           else{
                temp=true
                Toast.makeText(activity,"true", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(activity, "Error ${it.message}", Toast.LENGTH_SHORT).show()
        }
        return temp
    }

    //fun of upload image in storage fire store
    private fun uploadProfileImage(compressImageByte: ByteArray, onSuccess: (imagepath :String)->Unit) {

        val ref= userStorageRef.child("profileImages/${UUID.nameUUIDFromBytes(compressImageByte)}")
        ref.putBytes(compressImageByte).addOnCompleteListener{
            if(it.isSuccessful){
                onSuccess(ref.path)
            }
            else{
                Toast.makeText(activity,"Error : ${it.exception?.message.toString()}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    // fet the user information from fire store data base
    private fun getUserInfo(onComplete:(User) -> Unit){

        userDocRef.get().addOnSuccessListener {
                  onComplete(it.toObject(User::class.java)!!)

        }.addOnFailureListener {
            Toast.makeText(activity, "Error ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


}



