import {useEffect, useState} from "react";
import axios from "axios";
import {Alert} from "reactstrap";
import Card from "react-bootstrap/Card";
import button from "bootstrap/js/src/button";
import FriendRequestCard from "../friendRequestCard/FriendRequestCard";

const PendingFriendCard = (props) => {

    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    const handleAlertToggle = () => {
        setError(null);
    }

    useEffect(() => {
        setLoading(true);
        axios.get(`http://localhost:8080/users/pendingFriendRequests/${props.userId}`).then((response) => {
            setLoading(false);
            setData(response.data);
            setError(null);
        }).catch((err) => {
            setLoading(false);
            if (err.response) {
                setError(err.response.data);
            } else {
                setError(err.message);
            }
        });
    }, []);

    return (
        <>
            <h2>Pending Friend Requests</h2>
            {!!error && (
                <Alert color={"danger"} toggle={handleAlertToggle}>
                    Error: {error}
                </Alert>
            )}
            {loading && <div>Loading pending friend requests...</div>}
            {!loading && data ? (
                <div>
                    {data.map((friendInfo) => (
                        <Card key={friendInfo.id} style={{width: '30rem'}}>
                            <Card.Body>
                                {Object.values(friendInfo.pendingFriends)[0] === true ? (
                                        <div style={{display: "flex", justifyContent: "space-between"}}>
                                            {friendInfo.firstName} {friendInfo.lastName}
                                            <FriendRequestCard userId={props.userId} friendEmail={friendInfo.email}/>
                                        </div>
                                    ) :
                                    <div style={{display: "flex", justifyContent: "space-between"}}>
                                        {friendInfo.firstName} {friendInfo.lastName} &nbsp;&nbsp;&nbsp;
                                        <button disabled>Friend request sent</button>
                                    </div>
                                }
                            </Card.Body>
                        </Card>
                    ))}
                </div>
            ) : null}
        </>
    );
}

export default PendingFriendCard;