import Card from 'react-bootstrap/Card';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import DeleteOutlineOutlinedIcon from '@mui/icons-material/DeleteOutlineOutlined';
import {useState} from "react";
import {updateMsg, deleteMsg} from "../../store/board";
import {useDispatch} from "react-redux";
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
 * @returns {JSX.Element}
 * @constructor
 */
const BirthdayCard = (props) => {
    const [isEditing, setEditing] = useState(false);
    const [input, setInput] = useState("");

    const dispatch = useDispatch();

    const handleChange = (event) => {
        setInput(event.target.value);
    };

    const handleSubmit = (event) => {
        console.log(`update msg ${props.msgId} to ${input}`);
        const body = {msgText: input};
        const data = {boardId: props.boardId, msgId: props.msgId, body};
        dispatch(updateMsg(data));
    }

    const handleDelete = () => {
        const data = {boardId: props.boardId, msgId: props.msgId};
        dispatch(deleteMsg(data));
    }

    return (
        <div Style="padding: 0.3rem;">
        <Card style={{ width: '18rem' }}>
        <Card.Body>
            {!isEditing && (
                <>
                    <EditOutlinedIcon onClick={()=>setEditing(true)}/>
                    <DeleteOutlineOutlinedIcon onClick={handleDelete}/>
                </>
            )}
            {isEditing && (
                <form onSubmit={handleSubmit}>
                    <input type={"text"} value={input} onChange={handleChange}/>
                    <Button variant="outlined" size="small" type={"submit"} value={"Submit"}/>
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

export default BirthdayCard