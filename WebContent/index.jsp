<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iACADEMY Library</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="icon" type="image/png" href="images/logofooter.png">
    <link href="https://fonts.googleapis.com/css?family=Poppins:100,200,300,400,600,700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<body class="no-scroll">

<section class="hero">

    <nav class="navbar">
        <a class="logo" href="index.jsp"><img src="images/logo.png" alt="iACADEMY"></a>

        <div class="nav-links" id="navLinks">
            <ul>
                <li><a href="index.jsp" class="active">Home</a></li>
                <li><a href="index.jsp">Authors</a></li>
                <li><a href="index.jsp">Community</a></li>
                <li><a href="index.jsp">Contribute</a></li>
            </ul>
        </div>

        <div class="nav-actions">
            <a href="views/login.jsp" class="btn-outline">Log In</a>
            <a href="views/signup.jsp" class="btn-solid">Sign Up</a>
        </div>
    </nav>

    <div class="hero-text">
        <h1>Discover Books and Their <span class="highlight">Contributors</span> Effortlessly</h1>
        <p>Welcome to iACADEMY Library, game changers go-to platform for exploring authors' contributions to books.
            Search for forewords, prefaces, and endorsements with ease.</p>

        <form class="hero-search" action="SearchServlet" method="get">
            <input type="text" name="query" placeholder="Search a title, author, or contribution...">
            <button type="submit">Search</button>
        </form>
    </div>

    <div class="book-fan">
        <div class="watermark">iACADEMY LIBRARY</div>
        <a href="views/login.jsp" class="book-spine b1" style="background-image:url('https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1612238791i/56916837.jpg');"></a>
        <a href="views/login.jsp" class="book-spine b2" style="background-image:url('https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1489957987i/34626431.jpg');"></a>
        <a href="views/login.jsp" class="book-spine b3" style="background-image:url('https://m.media-amazon.com/images/I/81Scutrtj4L._UF1000,1000_QL80_.jpg');"></a>
        <a href="views/login.jsp" class="book-spine b4" style="background-image:url('https://www.penguin.co.uk/_next/image?url=https%3A%2F%2Fcdn.penguin.co.uk%2Fdam-assets%2Fbooks%2F9780141036144%2F9780141036144-jacket-large.jpg&w=614&q=100');"></a>
        <a href="views/login.jsp" class="book-spine b5" style="background-image:url('https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1650033243i/41733839.jpg');"></a>
        <a href="views/login.jsp" class="book-spine b6" style="background-image:url('https://m.media-amazon.com/images/I/71I-zd7XWkL._UF1000,1000_QL80_.jpg');"></a>
        <a href="views/login.jsp" class="book-spine b7" style="background-image:url('https://m.media-amazon.com/images/I/71EbsuhySYL._AC_UF1000,1000_QL80_.jpg');"></a>

    </div>

</section>



</body>
</html>
