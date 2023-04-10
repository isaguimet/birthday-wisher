import Container from "react-bootstrap/Container";
import {Alert} from "reactstrap";
import {useEffect} from "react";
import axiosInstance from "../../utils/API";
import Card from "react-bootstrap/Card";

/**
 * A component for rendering a User friends list
 * @returns {JSX.Element}
 * @constructor
 */
const FriendBirthdayList = (props) => {

    useEffect(() => {
        props.setLoading(true);
        axiosInstance.get(`https://proxy1-ey7sfy2hcq-wl.a.run.app/users/friendList/${props.loggedInUser}`).then((response) => {
            props.setLoading(false);
            props.setData(response.data);
            props.setError(null);
        }).catch((err8080) => {
            if (err8080.response) {
                props.setLoading(false);
                props.setError(err8080.response.data);
            } else {
                axiosInstance.get(`https://proxy2-ey7sfy2hcq-wl.a.run.app/users/friendList/${props.loggedInUser}`).then((response) => {
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
            }
        });
    }, []);

    const handleAlertToggle = () => {
        props.setError(null);
    };

    return (
        <Container>
            <h2>Friends birthdays</h2>
            {!!props.error && (
                <Alert color={"danger"} toggle={handleAlertToggle}>
                    Error: {props.error}
                </Alert>
            )}
            {props.loading && <div>Loading your friends...</div>}
            {!props.loading && props.data ? (
                <div className="friendList">
                    {props.data.map((friend) => (
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
        </Container>
    );
};

export default FriendBirthdayList;