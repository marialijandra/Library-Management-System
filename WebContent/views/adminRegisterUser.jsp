<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Without this, the register-a-user form (and the ability to submit it)
    // was reachable by just opening this URL directly, no login required.
    if (!"admin".equals(session.getAttribute("role"))) {
        response.sendRedirect(request.getContextPath() + "/views/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register User - iACADEMY Library Admin</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/images/logofooter.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link href="https://fonts.googleapis.com/css?family=Poppins:100,200,300,400,600,700&display=swap" rel="stylesheet">
</head>
<body>

<section class="auth-section">
    <div class="auth-card" id="registerCard" style="max-width:480px;">

        <div id="registerFormView">
            <a href="${pageContext.request.contextPath}/index.jsp">
                <img class="auth-logo" src="${pageContext.request.contextPath}/images/logofooter.png" alt="iACADEMY">
            </a>
            <h1>Register New User</h1>
            <p class="subtitle">New user? Register their account here.</p>

            <form id="registerForm" onsubmit="showRegisterConfirmation(event)">
                <div class="form-row">
                    <div class="form-group">
                        <label for="firstName">First Name</label>
                        <input type="text" id="firstName" name="firstName" placeholder="First Name" required>
                    </div>
                    <div class="form-group">
                        <label for="surname">Surname</label>
                        <input type="text" id="surname" name="surname" placeholder="Surname" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" placeholder="user@iacademy.edu.ph" required>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <div style="position:relative;">
                        <input type="password" id="password" name="password" placeholder="Create a password" required style="padding-right:44px;">
                        <button type="button" onclick="togglePasswordVisibility()" style="position:absolute; right:14px; top:50%; transform:translateY(-50%); background:none; border:none; cursor:pointer; padding:0; color:#666; display:flex; align-items:center;">
                            <svg id="eyeIcon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                <circle cx="12" cy="12" r="3"/>
                            </svg>
                        </button>
                    </div>
                </div>

                <div class="form-group">
                    <label for="role">Role</label>
                    <select id="role" name="role" required>
                        <option value="" disabled selected>Select a role</option>
                        <option value="student">Student</option>
                        <option value="librarian">Librarian</option>
                        <option value="admin">Admin</option>
                    </select>
                </div>

                <button type="submit" class="auth-submit" style="margin-top:5px;">Register User</button>
            </form>
        </div>

        <div id="registerConfirmView" style="display:none; text-align:center;">
            <div style="width:64px; height:64px; border-radius:50%; background:rgba(46,75,155,.12); display:flex; align-items:center; justify-content:center; margin:0 auto 22px;">
                <svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M4 12.5L9 17.5L20 6.5" stroke="#22356C" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </div>
            <h1 style="margin-bottom:12px;">User Registered</h1>
            <p class="subtitle" id="registerConfirmMessage" style="margin-bottom:32px; line-height:1.6;"></p>
            <button type="button" class="auth-submit" onclick="registerAnother()" style="margin-bottom:12px;">Register Another User</button>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn-outline" style="display:block; text-align:center; box-sizing:border-box; color:#22356C; border-color:#22356C;">Back to Home</a>
        </div>

    </div>
</section>

<script>
    function togglePasswordVisibility() {
        var input = document.getElementById('password');
        var icon = document.getElementById('eyeIcon');
        if (input.type === 'password') {
            input.type = 'text';
            icon.innerHTML = '<path d="M17.94 17.94A10.94 10.94 0 0 1 12 20c-7 0-11-8-11-8a18.5 18.5 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/>';
        } else {
            input.type = 'password';
            icon.innerHTML = '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>';
        }
    }

    function showRegisterConfirmation(event) {
        event.preventDefault();

        var form = document.getElementById('registerForm');
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
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                var first = document.getElementById('firstName').value;
                var surname = document.getElementById('surname').value;
                var email = document.getElementById('email').value;
                var role = document.getElementById('role').value;
                var roleLabel = role.charAt(0).toUpperCase() + role.slice(1);

                document.getElementById('registerConfirmMessage').innerHTML =
                    first + ' ' + surname + ' has been registered as a <strong>' + roleLabel + '</strong>. ' +
                    'A confirmation email has been sent to ' + email + '.';

                document.getElementById('registerFormView').style.display = 'none';
                document.getElementById('registerConfirmView').style.display = 'block';
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            console.error('Network Error:', error);
            alert("An unexpected network error occurred while reaching the server.");
        });
    }

    function registerAnother() {
        document.getElementById('registerForm').reset();
        document.getElementById('registerConfirmView').style.display = 'none';
        document.getElementById('registerFormView').style.display = 'block';
    }
</script>
</body>
</html>
