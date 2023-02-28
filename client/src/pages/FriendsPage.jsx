import Theme from "../theme/Theme";
import FriendCard from "../components/friendCard/FriendCard";
import {StyledDiv} from "./ProfilePage.style";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

const FriendsPage = (props) => {

    return (
        <Theme>
            <StyledDiv>
                <Container>
                    <Row>
                        <Col>
                            <h1>Friends birthdays</h1>
                            <FriendCard></FriendCard>
                        </Col>
                        <Col>
                            <h1>Pending Friend Requests</h1>
                        </Col>
                    </Row>
                </Container>
            </StyledDiv>
        </Theme>
    );
};

export default FriendsPage;