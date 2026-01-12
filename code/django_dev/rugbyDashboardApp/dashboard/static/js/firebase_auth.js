// Import the functions you need from the SDKs you need
import { initializeApp } from "https://www.gstatic.com/firebasejs/11.2.0/firebase-app.js";
import { getAuth, signInWithEmailAndPassword } from "https://www.gstatic.com/firebasejs/11.2.0/firebase-auth.js";

// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
apiKey: "AIzaSyBhvNss7L8oBlnE4wgkuHO8uLXwCHf2sI8",
authDomain: "rugby-stats-40be8.firebaseapp.com",
databaseURL: "https://rugby-stats-40be8-default-rtdb.europe-west1.firebasedatabase.app",
projectId: "rugby-stats-40be8",
storageBucket: "rugby-stats-40be8.firebasestorage.app",
messagingSenderId: "976199094577",
appId: "1:976199094577:web:cc066bd8c5dc4abd782010",
measurementId: "G-YNNH1MGNSZ"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);

// Login Submission Handler
document.getElementById("loginForm").addEventListener("submit", function(event) {
    event.preventDefault();  // Prevent the form from submitting normally

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    // Sign in using Firebase Authentication
    signInWithEmailAndPassword(auth, email, password)
        .then((userCredential) => {
            const user = userCredential.user;

            // Get ID token after successful login
            user.getIdToken(true).then((idToken) => {
                if (idToken){
                    sendIdTokenToBackend(idToken);  // Send ID token to the backend for verification
                } else {
                    console.error("ID token is empty");
                    document.getElementById("error-message").textContent = "Failed to retrieve ID token.";
                }
            });
        })
        .catch((error) => {
            // Error handling (wrong password, etc.)
            console.error("Error: ", error.message);
            document.getElementById("error-message").textContent = "Authentication failed. Please check your email/password and try again.";
        });
});



function getCSRFToken() {
    // Accessed cookies for the current domain using document.cookie
    const csrfToken = document.cookie.match(/csrftoken=([^;]+)/);

    // Using a regular expression to find the "csrftoken" cookie and capture its value
    // MDN Web Docs - Regular Expressions - https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Regular_expressions
    return csrfToken ? csrfToken[1] : "";   // If match is found, return the token otherwise, return an empty string
}

function sendIdTokenToBackend(idToken) {
    // Send the ID token to the Django backend
    fetch("/login/", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRFToken": getCSRFToken()
        },
        body: JSON.stringify({ id_token: idToken })  // Send ID token to Django backend
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === "success") {
            window.location.href = "/";  // Redirect to dashboard
        } else {
            document.getElementById("error-message").textContent = data.message;
        }
    })
    .catch(error => {
        console.error("Error sending ID token to backend:", error);
        document.getElementById("error-message").textContent = "Failed to authenticate. Please try again.";
    });
}

