package com.example.quickbuy.screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.quickbuy.R
import com.example.quickbuy.model.UserProfile
import com.example.quickbuy.screen.authentication.AuthenticationActivity
import com.example.quickbuy.screen.components.GetCircularProgressLoadingIndicator
import com.example.quickbuy.viewmodel.AuthState
import com.example.quickbuy.viewmodel.AuthViewModel
import com.example.quickbuy.viewmodel.ProfileViewModel
import com.example.quickbuy.viewmodel.Result

@Composable
fun Profile(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {

    val uiState by authViewModel.uiState.observeAsState()
    val userProfile by profileViewModel.userProfile.collectAsState()
    val uiStateProfile by profileViewModel.uiState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is AuthState.Unauthenticated) {
            context.startActivity(Intent(context, AuthenticationActivity::class.java))
            (context as? Activity)?.finish()
        }
    }

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserData()
    }

    when (val state = uiStateProfile) {
        is Result.Loading -> GetCircularProgressLoadingIndicator()
        is Result.Error -> {
            Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
        }

        is Result.Success -> {
            ProfileScreenContent(
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                userProfile = userProfile
            )
        }
        else -> Unit
    }
}

@Composable
fun ProfileScreenContent(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    userProfile: UserProfile,
) {
    var isEditing by remember { mutableStateOf(false) }
    var editableProfile by remember { mutableStateOf(userProfile) }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(userProfile) {
        if (!isEditing) editableProfile = userProfile
    }

    BoxWithConstraints(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        val screenWidth = maxWidth
        val isWideScreen = screenWidth > 600.dp

        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Profile",
                fontSize = if (isWideScreen) 36.sp else 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            ProfileImage(userProfile = userProfile, isClickable = isEditing)
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = editableProfile.firstName,
                    onValueChange = { editableProfile = editableProfile.copy(firstName = it) },
                    readOnly = !isEditing,
                    label = { Text("First Name") },
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(15.dp))
                OutlinedTextField(
                    value = editableProfile.lastName,
                    onValueChange = { editableProfile = editableProfile.copy(lastName = it) },
                    readOnly = !isEditing,
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = editableProfile.email,
                onValueChange = {},
                readOnly = true,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = editableProfile.phoneNo,
                onValueChange = { editableProfile = editableProfile.copy(phoneNo = it) },
                readOnly = !isEditing,
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = editableProfile.address,
                onValueChange = { editableProfile = editableProfile.copy(address = it) },
                readOnly = !isEditing,
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    isEditing = !isEditing
                    if (!isEditing) {
                        profileViewModel.updateUserData(userProfile = editableProfile)
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isWideScreen) 60.dp else 48.dp)
                    .border(
                        2.dp,
                        if (isSystemInDarkTheme()) Color.White else Color.Black,
                        RoundedCornerShape(10.dp)
                    ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isEditing) "Save Profile" else "Edit Profile",
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            }
            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { authViewModel.signOut() },
                colors = ButtonDefaults.buttonColors(Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isWideScreen) 60.dp else 48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Log Out", color = Color.White)
            }
        }
    }
    val showToast by profileViewModel.showToast.collectAsState()
    if (showToast) {
        Toast.makeText(LocalContext.current, "Profile Updated", Toast.LENGTH_SHORT).show()
        profileViewModel.showToastSuccess()
    }
}

@Composable
fun ProfileImage(userProfile: UserProfile, isClickable: Boolean) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            userProfile.profilePictureUrl = selectedImageUri.toString()
        }
    }
    Box(
        modifier = Modifier
            .clickable(enabled = isClickable) {
                launcher.launch("image/*")
            }
    ) {
        if (selectedImageUri != null) {
            GlideImage1(
                url = selectedImageUri.toString(),
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
            )
        } else if (userProfile.profilePictureUrl?.isNotEmpty() == true) {
            GlideImage1(
                url = userProfile.profilePictureUrl.toString(),
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "Profile Icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
            )
        }
    }
}

@Composable
fun GlideImage1(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->
            Glide.with(imageView.context)
                .load(url)
                .into(imageView)
        },
        modifier = modifier
    )
}

