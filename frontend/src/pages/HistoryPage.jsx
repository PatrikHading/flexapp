import { useEffect, useState } from "react";
import { fetchTimeHistory } from "../services/auth";

const formatDateTime = (value) => {
    if (!value) return "-";

    try {
        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return value;
        }

        return date.toLocaleString("sv-SE");
    } catch {
        return value;
    }
};

function HistoryPage({ user }) {
    const [historyEntries, setHistoryEntries] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadHistory = async () => {
            try {
                setLoading(true);
                setError("");

                const data = await fetchTimeHistory(user.id);
                setHistoryEntries(Array.isArray(data) ? data : []);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        if (user?.id) {
            loadHistory();
        }
    }, [user]);

    if (loading) {
        return (
            <div className="app-card">
                <h1>Historik</h1>
                <p className="app-card-subtitle">Laddar historik...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="app-card">
                <h1>Historik</h1>
                <p className="app-message-error">{error}</p>
            </div>
        );
    }

    if (historyEntries.length === 0) {
        return (
            <div className="app-card">
                <h1>Historik</h1>
                <p className="app-card-subtitle">
                    Det finns ingen historik att visa ännu.
                </p>
            </div>
        );
    }

    return (
        <div className="page-section">
            <section className="app-card-hero">
                <h1 className="app-card-title">Historik</h1>
                <p className="app-card-subtitle">
                    Här ser du dina tidigare tidrapporter.
                </p>
            </section>

            {historyEntries.map((entry) => (
                <section key={entry.id} className="app-card">
                    <h2>{entry.workDate || "Okänt datum"}</h2>

                    <div className="app-grid">
                        <div className="app-info-box">
                            <span className="app-label">Check-in</span>
                            <span className="app-value">
                {formatDateTime(entry.checkInTime)}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Lunch ut</span>
                            <span className="app-value">
                {formatDateTime(entry.lunchOutTime)}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Lunch in</span>
                            <span className="app-value">
                {formatDateTime(entry.lunchInTime)}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Check-out</span>
                            <span className="app-value">
                {formatDateTime(entry.checkOutTime)}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Arbetade minuter</span>
                            <span className="app-value">
                {entry.workedMinutes ?? "-"}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Lunchminuter</span>
                            <span className="app-value">
                {entry.lunchMinutes ?? "-"}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Flex minuter</span>
                            <span className="app-value">
                {entry.flexMinutes ?? "-"}
              </span>
                        </div>
                    </div>
                </section>
            ))}
        </div>
    );
}

export default HistoryPage;