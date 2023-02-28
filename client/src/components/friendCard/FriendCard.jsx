import Card from 'react-bootstrap/Card';
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
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
            console.log("response data: " + JSON.stringify(response.data))
            setError(null);
        }).catch((err) => {
            setLoading(false);
            if (err.response) {
                setError(err.response.data);
            } else {
                setError(err.message);
            }
            console.log(error)
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
                        <Card style={{width: '30rem'}}>
                            <Card.Body className="friendCard">
                                <Card.Text>
                                    <Row>
                                        <Col>{friend.firstName} {friend.lastName}</Col>
                                        <Col>{friend.birthdate.split("-")[1]}/{friend.birthdate.split("-")[2]}</Col>
                                    </Row>
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