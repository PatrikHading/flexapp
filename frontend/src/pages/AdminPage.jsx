import { useEffect, useMemo, useState } from "react";
import { fetchAllUsers } from "../services/auth";

function AdminPage() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadUsers = async () => {
            try {
                setLoading(true);
                setError("");

                const data = await fetchAllUsers();
                setUsers(Array.isArray(data) ? data : []);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        loadUsers();
    }, []);

    const stats = useMemo(() => {
        const totalUsers = users.length;
        const adminCount = users.filter((user) => user.role === "ADMIN").length;
        const regularUserCount = users.filter((user) => user.role === "USER").length;
        const activeCount = users.filter((user) => user.active).length;

        return {
            totalUsers,
            adminCount,
            regularUserCount,
            activeCount,
        };
    }, [users]);

    if (loading) {
        return (
            <div className="app-card">
                <h1>Admin</h1>
                <p className="app-card-subtitle">Laddar användare...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="app-card">
                <h1>Admin</h1>
                <p className="app-message-error">{error}</p>
            </div>
        );
    }

    return (
        <div className="page-section">
            <section className="app-card-hero">
                <h1 className="app-card-title">Admin</h1>
                <p className="app-card-subtitle">
                    Här kan du se en översikt av användarna i systemet.
                </p>
            </section>

            <section className="app-grid">
                <div className="app-card">
                    <h2>Översikt</h2>
                    <div className="app-grid">
                        <div className="app-info-box">
                            <span className="app-label">Totalt antal användare</span>
                            <span className="app-value">{stats.totalUsers}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Admins</span>
                            <span className="app-value">{stats.adminCount}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Vanliga användare</span>
                            <span className="app-value">{stats.regularUserCount}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Aktiva konton</span>
                            <span className="app-value">{stats.activeCount}</span>
                        </div>
                    </div>
                </div>
            </section>

            <section className="app-card">
                <h2>Användare</h2>

                {users.length === 0 ? (
                    <p className="app-card-subtitle">Inga användare hittades.</p>
                ) : (
                    <div className="admin-user-list">
                        {users.map((user) => (
                            <div key={user.id} className="app-info-box">
                                <span className="app-label">Namn</span>
                                <span className="app-value">
                  {user.firstName} {user.lastName}
                </span>

                                <span className="app-label">E-post</span>
                                <span className="app-value">{user.email}</span>

                                <span className="app-label">Roll</span>
                                <span className="app-value">{user.role}</span>

                                <span className="app-label">Status</span>
                                <span className="app-value">
                  {user.active ? "Aktiv" : "Inaktiv"}
                </span>
                            </div>
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
}

export default AdminPage;