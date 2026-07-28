<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Log In - iACADEMY Library</title>
    <link rel="stylesheet" href="../css/style.css">
    <link rel="icon" type="image/png" href="images/logofooter.png">
    <link href="https://fonts.googleapis.com/css?family=Poppins:100,200,300,400,600,700&display=swap" rel="stylesheet">
</head>
<body>

<section class="auth-section">
    <div class="auth-card">
        <a href="../index.jsp"><img class="auth-logo" src="../images/logofooter.png" alt="iACADEMY"></a>
        <h1>Library Access</h1>
        <p class="subtitle">Log in to continue exploring iACADEMY Library.</p>

        <form id="loginForm" onsubmit="handleLogin(event)">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" placeholder="you@iacademy.edu.ph" required>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" placeholder="Enter your password" required>
            </div>

            <div class="form-options">
                <label style="display:flex; align-items:center; gap:6px;">
                    <input type="checkbox" name="remember" style="accent-color:#22356C;"> Remember me
                </label>
                <a href="${pageContext.request.contextPath}/forgotPassword.jsp">Forgot password?</a>
            </div>

            <button type="submit" class="auth-submit">Log In</button>
        </form>

        <div class="auth-switch">
            Don't have an account? <a href="${pageContext.request.contextPath}/views/signup.jsp">Sign up</a>
        </div>
    </div>
</section>

<script>
    function handleLogin(event) {
        event.preventDefault();

        var form = document.getElementById('loginForm');
        var formData = new FormData(form);
        var params = new URLSearchParams(formData);

        // Get the accurate context path from JSP
        var contextPath = "<%= request.getContextPath() %>";

        fetch(contextPath + '/login', {
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
                    // Safe redirection using the validated context path
                    window.location.href = contextPath + data.redirect;
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