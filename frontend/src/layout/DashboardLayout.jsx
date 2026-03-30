import { Outlet, NavLink, useLocation, useNavigate } from "react-router-dom";
import { logoutUser } from "../services/auth";

const pageTitles = {
    "/dashboard": "Dashboard",
    "/schedule": "Schema",
    "/time": "Tidrapportering",
    "/history": "Historik",
    "/profile": "Profil",
    "/admin": "Admin",
};

function DashboardLayout({ user, setUser }) {
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = async () => {
        await logoutUser();
        setUser(null);
        navigate("/login");
    };

    const currentTitle = pageTitles[location.pathname] || "FlexApp";

    return (
        <div className="page-shell">
            <aside className="page-sidebar">
                <div className="page-logo">
                    <div className="page-logo-title">FlexApp</div>
                    <div className="page-logo-subtitle">Arbetstid och schema</div>
                </div>

                <nav className="page-nav">
                    <NavLink
                        to="/dashboard"
                        className={({ isActive }) =>
                            `page-nav-link${isActive ? " active" : ""}`
                        }
                    >
                        Dashboard
                    </NavLink>

                    <NavLink
                        to="/schedule"
                        className={({ isActive }) =>
                            `page-nav-link${isActive ? " active" : ""}`
                        }
                    >
                        Schema
                    </NavLink>

                    <NavLink
                        to="/time"
                        className={({ isActive }) =>
                            `page-nav-link${isActive ? " active" : ""}`
                        }
                    >
                        Tidrapportering
                    </NavLink>

                    <NavLink
                        to="/history"
                        className={({ isActive }) =>
                            `page-nav-link${isActive ? " active" : ""}`
                        }
                    >
                        Historik
                    </NavLink>

                    <NavLink
                        to="/profile"
                        className={({ isActive }) =>
                            `page-nav-link${isActive ? " active" : ""}`
                        }
                    >
                        Profil
                    </NavLink>

                    {user?.role === "ADMIN" && (
                        <NavLink
                            to="/admin"
                            className={({ isActive }) =>
                                `page-nav-link${isActive ? " active" : ""}`
                            }
                        >
                            Admin
                        </NavLink>
                    )}
                </nav>

                <div className="page-sidebar-footer">
                    <button className="page-logout-button" onClick={handleLogout}>
                        Logga ut
                    </button>
                </div>
            </aside>

            <div className="page-main">
                <header className="page-topbar">
                    <div className="page-topbar-title">{currentTitle}</div>
                    <div className="page-topbar-user">
                        Inloggad som <strong>{user?.firstName} {user?.lastName}</strong>
                    </div>
                </header>

                <main className="page-content">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}

export default DashboardLayout;