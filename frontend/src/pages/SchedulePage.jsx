import { useEffect, useState } from "react";
import { fetchUserSchedule } from "../services/auth";

const formatTime = (time) => {
    if (!time) return "-";

    if (typeof time === "string" && time.includes(":")) {
        return time.slice(0, 5);
    }

    return time;
};

function SchedulePage({ user }) {
    const [schedule, setSchedule] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadSchedule = async () => {
            try {
                setLoading(true);
                setError("");

                const data = await fetchUserSchedule(user.id);
                setSchedule(Array.isArray(data) ? data[0] : data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        if (user?.id) {
            loadSchedule();
        }
    }, [user]);

    if (loading) {
        return (
            <div className="app-card">
                <h1>Schema</h1>
                <p className="app-card-subtitle">Laddar schema...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="app-card">
                <h1>Schema</h1>
                <p className="app-message-error">{error}</p>
            </div>
        );
    }

    if (!schedule) {
        return (
            <div className="app-card">
                <h1>Schema</h1>
                <p className="app-card-subtitle">
                    Det finns inget schema registrerat för dig ännu.
                </p>
            </div>
        );
    }

    return (
        <div className="page-section">
            <section className="app-card-hero">
                <h1 className="app-card-title">Mitt schema</h1>
                <p className="app-card-subtitle">
                    Här ser du ditt registrerade arbetsschema.
                </p>
            </section>

            <section className="app-card">
                <div className="app-grid">
                    <div className="app-info-box">
                        <span className="app-label">Starttid</span>
                        <span className="app-value">
              {formatTime(schedule.plannedStartTime)}
            </span>
                    </div>

                    <div className="app-info-box">
                        <span className="app-label">Sluttid</span>
                        <span className="app-value">
              {formatTime(schedule.plannedEndTime)}
            </span>
                    </div>

                    <div className="app-info-box">
                        <span className="app-label">Arbetstid</span>
                        <span className="app-value">
              {schedule.expectedWorkMinutes ?? "-"} min
            </span>
                    </div>

                    <div className="app-info-box">
                        <span className="app-label">Betald lunch</span>
                        <span className="app-value">
              {schedule.paidLunchMinutes ?? "-"} min
            </span>
                    </div>

                    <div className="app-info-box">
                        <span className="app-label">Datum</span>
                        <span className="app-value">{schedule.workDate || "-"}</span>
                    </div>

                    <div className="app-info-box">
                        <span className="app-label">Schema-ID</span>
                        <span className="app-value">{schedule.id || "-"}</span>
                    </div>
                </div>
            </section>
        </div>
    );
}

export default SchedulePage;