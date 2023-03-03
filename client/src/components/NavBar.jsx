import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import {useDispatch, useSelector} from "react-redux";
import {setLogout} from "../store/user";

const navBAR = () => {

    const dispatch = useDispatch();
    const isLoggedIn = Boolean(useSelector((state) => state.user));

    return (
        <Navbar expand={"lg"} bg={"dark"} variant={"dark"}>
            <Container>
                <Navbar.Brand href="/home">Birthday Wisher</Navbar.Brand>
                {isLoggedIn && (
                    <>
                        <Navbar.Toggle aria-controls="responsive-navbar-nav"/>
                        <Navbar.Collapse id="responsive-navbar-nav">
                            <Nav className="me-auto">
                                <Nav.Link href="/home">Home</Nav.Link>
                                <Nav.Link href="/wishing-center">Wishing Center</Nav.Link>
                                <Nav.Link href="/personal-board">Personal Board</Nav.Link>
                                <Nav.Link href="/profile">Profile</Nav.Link>
                            </Nav>
                            <Nav>
                                <Nav.Link href="/" onClick={() => dispatch(setLogout())}>
                                    Logout
                                </Nav.Link>
                            </Nav>
                        </Navbar.Collapse>
                    </>
                )}
            </Container>
        </Navbar>
    );
};

export default navBAR;