import { Link, Outlet, useNavigate } from "react-router-dom";
import { logoutUser } from "../services/auth";

function Layout({ user, setUser }) {
    const navigate = useNavigate();

    const handleLogout = async () => {
        await logoutUser();
        setUser(null);
        navigate("/login");
    };

    return (
        <div style={styles.page}>
            <header style={styles.header}>
                <div style={styles.logo}>FlexApp</div>

                <nav style={styles.nav}>
                    <Link to="/" style={styles.link}>
                        Dashboard
                    </Link>
                    <Link to="/schedule" style={styles.link}>
                        Schema
                    </Link>
                    <Link to="/time" style={styles.link}>
                        Tidrapportering
                    </Link>
                    <Link to="/history" style={styles.link}>
                        Historik
                    </Link>
                    <Link to="/profile" style={styles.link}>
                        Profil
                    </Link>
                </nav>

                <div style={styles.rightSection}>
          <span style={styles.userText}>
            {user?.firstName} {user?.lastName}
          </span>
                    <button style={styles.logoutButton} onClick={handleLogout}>
                        Logga ut
                    </button>
                </div>
            </header>

            <main style={styles.main}>
                <Outlet />
            </main>
        </div>
    );
}

const styles = {
    page: {
        minHeight: "100vh",
        background: "#f4f6f8",
    },
    header: {
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        padding: "16px 24px",
        background: "#ffffff",
        borderBottom: "1px solid #e5e7eb",
        gap: "20px",
        flexWrap: "wrap",
    },
    logo: {
        fontSize: "28px",
        fontWeight: "bold",
        color: "#111827",
    },
    nav: {
        display: "flex",
        gap: "16px",
        flexWrap: "wrap",
    },
    link: {
        textDecoration: "none",
        color: "#111827",
        fontWeight: "500",
    },
    rightSection: {
        display: "flex",
        alignItems: "center",
        gap: "12px",
        flexWrap: "wrap",
    },
    userText: {
        color: "#374151",
        fontSize: "14px",
    },
    logoutButton: {
        padding: "10px 14px",
        fontSize: "14px",
        borderRadius: "8px",
        border: "none",
        cursor: "pointer",
        background: "#222",
        color: "#fff",
    },
    main: {
        padding: "24px",
        maxWidth: "1100px",
        margin: "0 auto",
    },
};

export default Layout;