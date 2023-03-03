import Card from 'react-bootstrap/Card';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlineOutlinedIcon from '@mui/icons-material/DeleteOutlineOutlined';
import {useState} from "react";
import axios from "axios";
import {useSelector} from "react-redux";
import Button from '@mui/material/Button';

/**
 * A component for rendering a User Birthday Message.
 * @param props {Object} Expected props for this component:
 * - boardId {string} the unique identifier for the board this message belongs to in the database.
 * - msgId {string} the unique identifier for this message in the database.
 * - fromUserId {string} the ID of the User who the message is from.
 * - toUserId {string} the ID of the User who the message is to.
 * - lastUpdatedDate {string} the date on which the message was last updated.
 * - msgText {string} the text content of the message.
 * - setLoading {function} function to set loading in a parent component.
 * - setData {function} function to set data in a parent component.
 * - setError {function} function to set error in a parent component.
 * - profileUser {string} the unique identifier for the user whose board this component is displaying.
 * @returns {JSX.Element}
 * @constructor
 */
const BirthdayCard = (props) => {

    // the ID of the user that is accessing this page
    const loggedInUser = useSelector((state) => state.user.id);
    const loggedInUserIsProfileUser = loggedInUser === props.profileUser;
    const loggedInUserIsMsgCreator = loggedInUser === props.fromUserId;

    const [isEditing, setEditing] = useState(false);
    const [input, setInput] = useState("");

    const handleChange = (event) => {
        setInput(event.target.value);
    };

    const handleSubmit = (event) => {
        const body = {msgText: input};
        props.setLoading(true);
        axios.patch(`http://localhost:8080/boards/${props.boardId}/messages/${props.msgId}`, body).then((response) => {
            props.setLoading(false);
            props.setData(response.data);
            props.setError(null);
        }).catch((err) => {
            props.setLoading(false);
            //props.setData(null);
            if (err.response) {
                props.setError(err.response.data);
            } else {
                props.setError(err.message);
            }
        });
    };

    const handleDelete = () => {
        props.setLoading(true);
        axios.delete(`http://localhost:8080/boards/${props.boardId}/messages/${props.msgId}`).then((response) => {
            props.setLoading(false);
            props.setData(response.data);
            props.setError(null);
        }).catch((err) => {
            props.setLoading(false);
            //props.setData(null);
            if (err.response) {
                props.setError(err.response.data);
            } else {
                props.setError(err.message);
            }
        });
    };

    return (
        <div style={{padding: "0.3rem"}}>
            <Card style={{width: '18rem'}}>
                <Card.Body>
                    {!isEditing && <>
                        {loggedInUserIsMsgCreator &&
                            <EditOutlinedIcon onClick={() => setEditing(true)}/>
                        }
                        {(loggedInUserIsMsgCreator || loggedInUserIsProfileUser) && (
                            <DeleteOutlineOutlinedIcon onClick={handleDelete}/>
                        )}
                    </>}
                    {isEditing && (
                        <form onSubmit={handleSubmit}>
                            <input type={"text"} value={input} onChange={handleChange}/>
                            <div style={{paddingTop: "0.3rem"}}>
                              <Button variant={"outlined"} size={"small"} type={"submit"} value={"Submit"}/>
                            </div>
                        </form>
                    )}
                    <Card.Text>
                        {props.msgText}
                    </Card.Text>
                </Card.Body>
            </Card>
        </div>
    );
};

export default BirthdayCard;
