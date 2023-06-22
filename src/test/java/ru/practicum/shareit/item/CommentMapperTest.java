package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toCommentDto() {
        User user = new User(1L, "Name", "mail@zz.zz");
        User commentator = new User(2L, "Commentator", "commentator@zz.zz");
        Item item = new Item(1L, "Item", "Desc", true, user, 0);
        Comment comment = new Comment(1L, "Text of comment", item, commentator, LocalDateTime.now());

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertEquals("Text of comment", commentDto.getText());
        assertEquals("Commentator", commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    void toComment() {
        User user = new User(1L, "Name", "mail@zz.zz");
        User commentator = new User(2L, "Commentator", "commentator@zz.zz");
        Item item = new Item(1L, "Item", "Desc", true, user, 0);
        CommentDto commentDto = new CommentDto(1L, "Text", "Commentator", LocalDateTime.now());

        Comment comment = CommentMapper.toComment(commentDto, commentator, item, commentDto.getCreated());

        assertEquals("Text", comment.getText());
        assertEquals(item.getName(), comment.getItem().getName());
    }
}