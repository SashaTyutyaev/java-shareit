package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item1;
    private Item item2;
    private User user1;
    private Comment comment1;
    private Comment comment2;

    @Test
    void findAllByItemId() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);
        item2 = createItem(2, "NewName", "NewDescription", true, user1, null);
        comment1 = createComment(1, "comment", LocalDateTime.now(), item1, user1);
        comment2 = createComment(2, "comment2", LocalDateTime.now(), item2, user1);

        List<Comment> comments = commentRepository.findAllByItemId(item1.getId());
        assertEquals(1, comments.size());
        assertEquals(comment1, comments.get(0));

        List<Comment> comments2 = commentRepository.findAllByItemId(item2.getId());
        assertEquals(1, comments2.size());
        assertEquals(comment2, comments2.get(0));

        List<Comment> emptyComment = commentRepository.findAllByItemId(10);
        assertTrue(emptyComment.isEmpty());
    }

    @Test
    void findAllByItemIdWithoutComment() {
        user1 = createUser(1, "Name", "name@mail.ru");
        item1 = createItem(1, "Name", "Description", true, user1, null);

        List<Comment> emptyComment = commentRepository.findAllByItemId(item1.getId());
        assertTrue(emptyComment.isEmpty());
    }

    private Item createItem(Integer id, String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .request(request)
                .owner(owner)
                .build();

        return itemRepository.save(item);
    }

    private Comment createComment(Integer id, String text, LocalDateTime createdDate, Item item, User author) {
        Comment comment = Comment.builder()
                .id(id)
                .text(text)
                .createdDate(createdDate)
                .item(item)
                .author(author)
                .build();

        return commentRepository.save(comment);
    }

    private User createUser(Integer id, String name, String email) {
        User user = User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();

        return userRepository.save(user);
    }
}