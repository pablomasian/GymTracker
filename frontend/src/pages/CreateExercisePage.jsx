// Página para proponer un nuevo ejercicio (queda pendiente de aprobación)
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createExercise } from "../backend/exerciseService";

export default function CreateExercisePage() {
    const nav = useNavigate();
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [muscles, setMuscles] = useState("");
    const [equipment, setEquipment] = useState("");
    const [imageUrl, setImageUrl] = useState("");
    const [exerciseType, setExerciseType] = useState("STRENGTH"); // Nuevo campo

    const [busy, setBusy] = useState(false);
    const [err, setErr] = useState("");
    const [successMsg, setSuccessMsg] = useState("");

    const valid = name.trim().length >= 2;

    async function onSubmit(e) {
        e.preventDefault();
        if (!valid || successMsg) return;

        setErr("");
        setBusy(true);

        const payload = {
            name: name.trim(),
            description: description.trim(),
            muscles: muscles.trim(),
            equipment: equipment.trim(),
            imageUrl: imageUrl.trim(),
            exerciseType: exerciseType // Incluir tipo de ejercicio
        };

        createExercise(
            payload,
            () => {
                setSuccessMsg("Proposal submitted! Thank you for your contribution.");
                setBusy(false);
                setName("");
                setDescription("");
                setMuscles("");
                setEquipment("");
                setImageUrl("");
                setExerciseType("STRENGTH");
            },
            (errorPayload) => {
                setErr(errorPayload?.globalError || "Error submitting proposal. The exercise might already exist or be pending approval.");
                setBusy(false);
            }
        );
    }

    return (
        <div className="page">
            <div className="card" style={{ width: "min(600px, 95vw)", margin: "0 auto" }}>
                <div className="card-header">
                    <h2 className="card-title">Propose New Exercise</h2>
                    <div className="card-sub">Your proposal will be reviewed by an administrator before it's added to the catalog.</div>
                </div>
                <div className="card-body">
                    {successMsg ? (
                        <div className="stack" style={{ textAlign: 'center' }}>
                            <div className="banner" style={{ background: '#e8f5e9', color: '#2e7d32', border: '1px solid #c8e6c9' }}>
                                ✅ {successMsg}
                            </div>
                            <button className="btn" type="button" onClick={() => nav('/routines')}>
                                Back to Routines
                            </button>
                        </div>
                    ) : (
                        <form onSubmit={onSubmit} className="stack" noValidate>
                            {err && <div className="banner">⚠️ {err}</div>}

                            <div>
                                <label className="label" htmlFor="name">Exercise Name</label>
                                <input
                                    id="name"
                                    className="input"
                                    value={name}
                                    onChange={e => setName(e.target.value)}
                                    placeholder="e.g., Dumbbell Lateral Raises"
                                    minLength={2}
                                    required
                                    autoFocus
                                />
                            </div>

                            <div>
                                <label className="label" htmlFor="muscles">Main Muscles Worked</label>
                                <input
                                    id="muscles"
                                    className="input"
                                    value={muscles}
                                    onChange={e => setMuscles(e.target.value)}
                                    placeholder="e.g., Shoulders"
                                />
                            </div>

                            <div>
                                <label className="label" htmlFor="exerciseType">Exercise Type</label>
                                <select
                                    id="exerciseType"
                                    className="input"
                                    value={exerciseType}
                                    onChange={e => setExerciseType(e.target.value)}
                                    style={{ padding: '0.6rem', cursor: 'pointer' }}
                                >
                                    <option value="STRENGTH">💪 Strength</option>
                                    <option value="CARDIO">🏃 Cardio</option>
                                </select>
                            </div>

                            <div>
                                <label className="label" htmlFor="equipment">Equipment</label>
                                <input
                                    id="equipment"
                                    className="input"
                                    value={equipment}
                                    onChange={e => setEquipment(e.target.value)}
                                    placeholder="e.g., Dumbbells"
                                />
                            </div>

                            <div>
                                <label className="label" htmlFor="imageUrl">Image URL (Optional)</label>
                                <input
                                    id="imageUrl"
                                    className="input"
                                    type="url"
                                    value={imageUrl}
                                    onChange={e => setImageUrl(e.target.value)}
                                    placeholder="https://example.com/image.jpg or .gif"
                                />
                                {imageUrl && (
                                    <img
                                        src={imageUrl}
                                        alt="Preview"
                                        style={{
                                            marginTop: '0.5rem',
                                            maxWidth: '200px',
                                            maxHeight: '200px',
                                            borderRadius: '8px',
                                            border: '2px solid #ddd'
                                        }}
                                        onError={(e) => {
                                            e.target.style.display = 'none';
                                        }}
                                    />
                                )}
                            </div>

                            <div>
                                <label className="label" htmlFor="description">Description (Optional)</label>
                                <textarea
                                    id="description"
                                    className="input"
                                    value={description}
                                    onChange={e => setDescription(e.target.value)}
                                    rows={3}
                                    placeholder="Briefly explain how to perform the exercise..."
                                />
                            </div>

                            <div className="actions" style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem", marginTop: "1rem" }}>
                                <button className="btn btn-black" type="button" onClick={() => nav(-1)} disabled={busy}>
                                    Cancel
                                </button>
                                <button className="btn btn-primary" type="submit" disabled={!valid || busy}>
                                    {busy ? "Submitting..." : "Submit Proposal"}
                                </button>
                            </div>
                        </form>
                    )}
                </div>
            </div>
        </div>
    );
}