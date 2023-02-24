import Card from 'react-bootstrap/Card';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlineOutlinedIcon from '@mui/icons-material/DeleteOutlineOutlined';
import {useState} from "react";
import axios from "axios";

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
 * @returns {JSX.Element}
 * @constructor
 */
const BirthdayCard = (props) => {
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
            props.setData(null);
            props.setError(err.response.statusText);
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
            props.setData(null);
            props.setError(err.response.statusText);
        });
    };

    return (
        <Card style={{width: '18rem'}}>
            <Card.Body>
                {!isEditing && (
                    <>
                        <EditOutlinedIcon onClick={() => setEditing(true)}/>
                        <DeleteOutlineOutlinedIcon onClick={handleDelete}/>
                    </>
                )}
                {isEditing && (
                    <form onSubmit={handleSubmit}>
                        <input type={"text"} value={input} onChange={handleChange}/>
                        <input type={"submit"} value={"Submit"}/>
                    </form>
                )}
                <Card.Text>
                    {props.msgText}
                </Card.Text>
            </Card.Body>
        </Card>
    );
};

export default BirthdayCard;