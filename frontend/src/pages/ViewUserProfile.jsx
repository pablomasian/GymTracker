import { useParams } from "react-router-dom";
import PublicProfile from "./PublicProfile";

export default function ViewUserProfile() {
  const { id } = useParams();
  return <PublicProfile id={id} isCoach={false} />;
}
