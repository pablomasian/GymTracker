import { useParams } from "react-router-dom";
import PublicProfile from "./PublicProfile";

export default function ViewCoachProfile() {
  const { id } = useParams();
  return <PublicProfile id={id} isCoach={true} />;
}
