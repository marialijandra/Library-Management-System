<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up - iACADEMY Library</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/images/logofooter.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link href="https://fonts.googleapis.com/css?family=Poppins:100,200,300,400,600,700&display=swap" rel="stylesheet">
</head>
<body>

<section class="auth-section">
    <div class="auth-card" id="signupCard" style="max-width:480px;">

        <div id="signupFormView">
            <a href="${pageContext.request.contextPath}/index.jsp">
                <img class="auth-logo" src="${pageContext.request.contextPath}/images/logofooter.png" alt="iACADEMY">
            </a>
            <h1>Create Account</h1>
            <p class="subtitle">Join iACADEMY Library to save authors and contribute.</p>

            <form id="signupForm" onsubmit="handleSignup(event)">
                <!-- Hidden input for role default to student for self-registration -->
                <input type="hidden" name="role" value="student">

                <div class="form-row">
                    <div class="form-group">
                        <label for="firstName">First Name</label>
                        <input type="text" id="firstName" name="firstName" placeholder="First Name" required>
                    </div>
                    <div class="form-group">
                        <label for="lastName">Last Name</label>
                        <!-- Named "surname" to match DB column and RegisterServlet parameter -->
                        <input type="text" id="lastName" name="surname" placeholder="Last Name" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" placeholder="you@iacademy.edu.ph" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" placeholder="Create a password" required>
                    </div>
                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <input type="password" id="confirmPassword" placeholder="Re-enter password" required>
                    </div>
                </div>

                <div class="checkbox-group">
                    <input type="checkbox" id="agreement" name="agreement" required>
                    <label for="agreement">By signing up, I consent to the processing of my data in accordance with the school's policies.</label>
                </div>

                <button type="submit" class="auth-submit">Sign Up</button>
            </form>

            <div class="auth-switch">
                Already have an account? <a href="${pageContext.request.contextPath}/views/login.jsp">Log in</a>
            </div>
        </div>

        <div id="signupConfirmView" style="display:none; text-align:center;">
            <div style="width:64px; height:64px; border-radius:50%; background:rgba(46,75,155,.12); display:flex; align-items:center; justify-content:center; margin:0 auto 22px;">
                <svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M4 12.5L9 17.5L20 6.5" stroke="#22356C" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </div>
            <h1 style="margin-bottom:12px;">Account Created</h1>
            <p class="subtitle" style="margin-bottom:32px; line-height:1.6;">
                Your student account has been successfully created.<br>
                Please log in using your credentials to access the library.
            </p>
            <a href="${pageContext.request.contextPath}/views/login.jsp" class="auth-submit" style="display:block; text-decoration:none; box-sizing:border-box;">Go to Log In</a>
        </div>

    </div>
</section>

<script>
    function handleSignup(event) {
        event.preventDefault();

        var password = document.getElementById('password').value;
        var confirmPassword = document.getElementById('confirmPassword').value;

        if (password !== confirmPassword) {
            alert("Passwords do not match!");
            return;
        }

        var form = document.getElementById('signupForm');
        var formData = new FormData(form);
        var params = new URLSearchParams(formData);

        fetch('${pageContext.request.contextPath}/register', {
            method: 'POST',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Connection failed');
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                document.getElementById('signupFormView').style.display = 'none';
                document.getElementById('signupConfirmView').style.display = 'block';
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            console.error('Network Error:', error);
            alert("An unexpected network error occurred while reaching the server.");
        });
    }
</script>

</body>
</html>
