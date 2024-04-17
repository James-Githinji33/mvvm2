package net.ezra.ui.students


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter

import coil.request.ImageRequest
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import net.ezra.navigation.ROUTE_ADD_STUDENTS
import net.ezra.navigation.ROUTE_HOME
import net.ezra.navigation.ROUTE_PRODUCTS
import java.util.UUID
import android.widget.Toast;
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.res.painterResource
import net.ezra.MainActivity
import net.ezra.R
import net.ezra.ui.home.HomeScreen


@Composable
fun AddStudents(navController: NavHostController) {
Box(modifier = Modifier.fillMaxSize()) {
    Image(painter = painterResource(id = R.drawable.img_1),
        contentDescription = "",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop

    )



                LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {

            Column(

                modifier = Modifier.padding(15.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ROUTE_HOME) {
                                popUpTo(ROUTE_ADD_STUDENTS) { inclusive = true }
                            }
                        },
                    color = Color.Blue,
                    text = "go to Home screen"

                )

                Text(text = "Register Student")

                var photoUri: Uri? by remember { mutableStateOf(null) }
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    photoUri = uri
                }

                var studentName by rememberSaveable {
                    mutableStateOf("")
                }

                var studentClass by rememberSaveable {
                    mutableStateOf("")
                }

                var studentLocation by rememberSaveable {
                    mutableStateOf("")
                }

                var studentEmail by rememberSaveable {
                    mutableStateOf("")
                }

                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text(text = "Name") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = studentClass,
                    onValueChange = { studentClass= it },
                    label = { Text(text = "Class") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = studentLocation,
                    onValueChange = { studentLocation= it },
                    label = { Text(text = "Location") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = studentEmail,
                    onValueChange = { studentEmail= it },
                    label = { Text(text = "Email") },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )




                OutlinedButton(
                    onClick = {
                        launcher.launch(
                            PickVisualMediaRequest(
                                //Here we request only photos. Change this to .ImageAndVideo if you want videos too.
                                //Or use .VideoOnly if you only want videos.
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                ) {
                    Text("Select Image")
                }


                if (photoUri != null) {
                    //Use Coil to display the selected image
                    val painter = rememberAsyncImagePainter(
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(data = photoUri)
                            .build()
                    )

                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .size(150.dp)
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray),
                        contentScale = ContentScale.Crop,

                        )
                }


                OutlinedButton(onClick = {
                    photoUri?.let { uploadImageToFirebaseStorage(it, studentName, studentClass, studentLocation, studentEmail) }

                }) {

                    Text(text = "Register")


                }











            }
        }
    }
}


}



fun uploadImageToFirebaseStorage(imageUri: Uri, studentName: String, studentClass: String, studentLocation: String, studentEmail: String) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("images/${UUID.randomUUID()}")

    val uploadTask = imageRef.putFile(imageUri)
    uploadTask.continueWithTask { task ->
        if (!task.isSuccessful) {
            task.exception?.let {
                throw it
            }
        }
        imageRef.downloadUrl
    }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result
            saveToFirestore(downloadUri.toString(), studentName, studentClass, studentLocation, studentEmail)
        } else {


        }
    }
}

fun saveToFirestore(imageUrl: String, studentName: String, studentClass: String, studentLocation: String, studentEmail: String) {
    val db = Firebase.firestore
    val imageInfo = hashMapOf(
        "imageUrl" to imageUrl,
        "studentName" to studentName,
        "studentClass" to studentClass,
        "studentLocation" to studentLocation,
        "studentEmail" to studentEmail


       
    )
    



    db.collection("Students")
        .add(imageInfo)
        .addOnSuccessListener {



        }
        .addOnFailureListener {
            // Handle error
        }
}







@Preview(showBackground = true)
@Composable
fun PreviewLight() {
    AddStudents(rememberNavController())
}




