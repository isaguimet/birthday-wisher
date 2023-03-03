import Card from 'react-bootstrap/Card';
import {useEffect, useState} from "react";
import axios from "axios";
import {Alert} from "reactstrap";

/**
 * A component for rendering a User friends list
 * @param props {Object} Expected props for this component:
 * - userId {ObjectId} the unique identifier for a friend of a user (to be implemented!)
 * @returns {JSX.Element}
 * @constructor
 */
const FriendCard = (props) => {
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    const userId = "63f90f25dca51a6d00d65007";

    useEffect(() => {
        setLoading(true);
        axios.get(`http://localhost:8080/users/friendList/${userId}`).then((response) => {
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

    const handleAlertToggle = () => {
        setError(null);
    }

    return (
        <div>
            {!!error && (
                <Alert color={"danger"} toggle={handleAlertToggle}>
                    Error: {error}
                </Alert>
            )}
            {loading && <div>Loading friends...</div>}
            {!loading && data ? (
                <div className="friendList">
                    {data.map((friend) => (
                        <Card key={friend.id} style={{width: '30rem'}}>
                            <Card.Body className="friendCard">
                                <Card.Text>
                                    {friend.firstName} {friend.lastName} &nbsp;&nbsp;&nbsp;&nbsp;
                                    {friend.birthdate.split("-")[1]}/{friend.birthdate.split("-")[2]}
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    ))}
                </div>
            ) : null}
        </div>
    );
}

export default FriendCard;