import Card from 'react-bootstrap/Card';
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";

/**
 * A component for rendering a User friends list
 * @param props {Object} Expected props for this component:
 * - userId {ObjectId} the unique identifier for a friend of a user
 * - firstName {string} the firstName of a friend
 * - lastName {string} the lastName of a friend
 * - birthdate {string} the birthdate of a friend
 * - profilePic {string} the profile picture of a friend
 * @returns {JSX.Element}
 * @constructor
 */
const FriendCard = (props) => {
    const birthday = props.birthdate.split("-")
    const month = birthday[1]
    const day = birthday[2]

    return (
        <Card style={{width: '30rem'}}>
            <Card.Body>
                <Card.Text>
                    <Row>
                        <Col>{props.lastName} {props.firstName}</Col>
                        <Col>{month}/{day}</Col>
                    </Row>
                </Card.Text>
            </Card.Body>
        </Card>
    );
}

export default FriendCard;