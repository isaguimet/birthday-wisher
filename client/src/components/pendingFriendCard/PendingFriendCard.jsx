import {useEffect} from "react";
import {Alert} from "reactstrap";
import Card from "react-bootstrap/Card";
import button from "bootstrap/js/src/button";
import FriendRequestCard from "../friendRequestCard/FriendRequestCard";
import axiosInstance from "../../utils/API";

const PendingFriendCard = (props) => {

    const handleAlertToggle = () => {
        props.setError(null);
    }

    useEffect(() => {
        props.setLoading(true);
        axiosInstance.get(`users/pendingFriendRequests/${props.userId}`).then((response) => {
            props.setLoading(false);
            props.setData(response.data);
            props.setError(null);
        }).catch((err) => {
            props.setLoading(false);
            if (err.response) {
                props.setError(err.response.data);
            } else {
                props.setError(err.message);
            }
        });
    }, []);

    return (
        <>
            <h2>Pending Friend Requests</h2>
            {!!props.error && (
                <Alert color={"danger"} toggle={handleAlertToggle}>
                    Error: {props.error}
                </Alert>
            )}
            {props.loading && <div>Loading pending friend requests...</div>}
            {!(props.loading) && props.data ? (
                <div>
                    {props.data.map((friendInfo) => (
                        <Card key={friendInfo.id} style={{width: '30rem'}}>
                            <Card.Body>
                                {Object.keys(friendInfo.pendingFriends).map((userId) => (
                                    <div key={userId}>
                                        {userId === props.userId ? (
                                            <div>
                                                {friendInfo.pendingFriends[userId] ? (
                                                        <div style={{display: "flex", justifyContent: "space-between"}}>
                                                            {friendInfo.firstName} {friendInfo.lastName}
                                                            <button disabled>Friend request sent</button>
                                                        </div>
                                                    ) :
                                                    <div style={{display: "flex", justifyContent: "space-between"}}>
                                                        {friendInfo.firstName} {friendInfo.lastName}
                                                        <FriendRequestCard userId={props.userId}
                                                                           friendEmail={friendInfo.email}/>
                                                    </div>}
                                            </div>
                                        ) : null}
                                    </div>
                                ))}
                            </Card.Body>
                        </Card>
                    ))}
                </div>
            ) : null}
        </>
    );
}

export default PendingFriendCard;