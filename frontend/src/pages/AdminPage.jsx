import { useEffect, useMemo, useState } from "react";
import { fetchAllUsers, createUserAsAdmin } from "../services/auth";

function AdminPage() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("USER");
    const [active, setActive] = useState(true);

    const [createLoading, setCreateLoading] = useState(false);
    const [createError, setCreateError] = useState("");
    const [createSuccess, setCreateSuccess] = useState("");

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

    useEffect(() => {
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

    const handleCreateUser = async (e) => {
        e.preventDefault();
        setCreateError("");
        setCreateSuccess("");

        if (password.length < 6) {
            setCreateError("Lösenordet måste vara minst 6 tecken.");
            return;
        }

        try {
            setCreateLoading(true);

            const createdUser = await createUserAsAdmin({
                firstName,
                lastName,
                email,
                password,
                role,
                active,
            });

            setUsers((prevUsers) => [createdUser, ...prevUsers]);

            setFirstName("");
            setLastName("");
            setEmail("");
            setPassword("");
            setRole("USER");
            setActive(true);

            setCreateSuccess("Användaren har skapats.");
        } catch (err) {
            setCreateError(err.message);
        } finally {
            setCreateLoading(false);
        }
    };

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
                    Här kan du skapa användare och se en översikt av systemets konton.
                </p>
            </section>

            <section className="app-card">
                <h2>Skapa användare</h2>
                <p className="app-card-subtitle">
                    Lägg till en ny användare i systemet.
                </p>

                <form className="profile-form" onSubmit={handleCreateUser}>
                    <div className="profile-form-grid">
                        <div>
                            <label className="app-label">Förnamn</label>
                            <input
                                className="app-input"
                                type="text"
                                value={firstName}
                                onChange={(e) => setFirstName(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Efternamn</label>
                            <input
                                className="app-input"
                                type="text"
                                value={lastName}
                                onChange={(e) => setLastName(e.target.value)}
                                required
                            />
                        </div>

                        <div className="profile-form-full">
                            <label className="app-label">E-post</label>
                            <input
                                className="app-input"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Lösenord</label>
                            <input
                                className="app-input"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Roll</label>
                            <select
                                className="app-input"
                                value={role}
                                onChange={(e) => setRole(e.target.value)}
                            >
                                <option value="USER">USER</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>
                        </div>

                        <div className="profile-form-full">
                            <label className="admin-checkbox-row">
                                <input
                                    type="checkbox"
                                    checked={active}
                                    onChange={(e) => setActive(e.target.checked)}
                                />
                                <span>Aktivt konto</span>
                            </label>
                        </div>
                    </div>

                    <div className="profile-form-actions">
                        <button className="app-button" type="submit" disabled={createLoading}>
                            {createLoading ? "Skapar..." : "Skapa användare"}
                        </button>
                    </div>

                    {createError && <p className="app-message-error">{createError}</p>}
                    {createSuccess && <p className="app-message-success">{createSuccess}</p>}
                </form>
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