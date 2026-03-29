package org.example.socialnetwork.Service;

import org.example.socialnetwork.Domain.FriendRequest;
import org.example.socialnetwork.Domain.Friendship;
import org.example.socialnetwork.Domain.Message;
import org.example.socialnetwork.Domain.User;
import org.example.socialnetwork.Persistence.Entity.FriendshipEntity;
import org.example.socialnetwork.Persistence.Entity.MessageEntity;
import org.example.socialnetwork.Persistence.Entity.UserEntity;
import org.example.socialnetwork.Persistence.Repository.FriendshipJpaRepository;
import org.example.socialnetwork.Persistence.Repository.MessageJpaRepository;
import org.example.socialnetwork.Persistence.Repository.UserJpaRepository;
import org.example.socialnetwork.Utils.Events.ChangeEventType;
import org.example.socialnetwork.Utils.Events.UserEntityChangeEvent;
import org.example.socialnetwork.Utils.Observer.Observable;
import org.example.socialnetwork.Utils.Observer.Observer;
import org.example.socialnetwork.Utils.Pageable.Page;
import org.example.socialnetwork.Utils.Pageable.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class Service implements Observable<UserEntityChangeEvent> {
    private final UserJpaRepository userRepository;
    private final FriendshipJpaRepository friendshipRepository;
    private final MessageJpaRepository messageRepository;
    private final List<Observer<UserEntityChangeEvent>> observers = new ArrayList<>();

    public Service(UserJpaRepository userRepository,
                   FriendshipJpaRepository friendshipRepository,
                   MessageJpaRepository messageRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void addObserver(Observer<UserEntityChangeEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<UserEntityChangeEvent> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(UserEntityChangeEvent event) {
        observers.forEach(observer -> observer.update(event));
    }

    public List<FriendRequest> getFriendRequests(Long id) {
        return friendshipRepository.findAll().stream()
                .map(this::toDomainFriendship)
                .filter(friendship -> friendship.getId2().equals(id) && friendship.isPending())
                .map(friendship -> {
                    User user = findOneUser(friendship.getId1()).orElse(null);
                    assert user != null;
                    return new FriendRequest(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), friendship.getDate());
                })
                .collect(Collectors.toList());
    }

    public List<FriendRequest> getUsersFriendsDTO(Long id) {
        return friendshipRepository.findAll().stream()
                .map(this::toDomainFriendship)
                .filter(prietenie -> prietenie.contains(id) && !prietenie.isPending())
                .map(prietenie -> {
                    Long friendId = prietenie.getId1().equals(id) ? prietenie.getId2() : prietenie.getId1();
                    User friend = findOneUser(friendId).orElse(null);
                    if (friend != null) {
                        return new FriendRequest(prietenie.getId(), friend.getFirstName(), friend.getLastName(), friend.getEmail(), prietenie.getDate());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Page<Friendship> getUsersFriendsDTOOnPage(Pageable pageable, Long id) {
        org.springframework.data.domain.Page<FriendshipEntity> pageResult = friendshipRepository.findAcceptedForUser(
                id,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())
        );

        List<Friendship> friendships = pageResult.getContent().stream().map(entity -> {
            Friendship friendship = toDomainFriendship(entity);
            if (friendship.getId2().equals(id)) {
                friendship.setId1(entity.getUser2());
                friendship.setId2(entity.getUser1());
            }
            return friendship;
        }).toList();

        return new Page<>(friendships, (int) pageResult.getTotalElements());
    }

    public List<User> getUsersFriends(Long id) {
        return friendshipRepository.findAll().stream()
                .map(this::toDomainFriendship)
                .filter(prietenie -> prietenie.contains(id) && !prietenie.isPending())
                .map(prietenie -> {
                    Long friendId;
                    if (prietenie.getId1().equals(id)) {
                        friendId = prietenie.getId2();
                    } else {
                        friendId = prietenie.getId1();
                    }
                    return findOneUser(friendId).orElse(null);
                })
                .collect(Collectors.toList());
    }

    public User findOneUserByNameAndEmail(String firstName, String lastName, String email) {
        return userRepository.findAll().stream()
                .map(this::toDomainUser)
                .filter(user -> user.getFirstName().equals(firstName) && user.getLastName().equals(lastName) && user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDomainUser).toList();
    }

    public Optional<User> findOneUser(Long id) {
        return userRepository.findById(id).map(this::toDomainUser);
    }

    @Transactional
    public User addUser(User u) {
        if (userRepository.existsByEmail(u.getEmail())) {
            throw new ServiceException("Email already in use!");
        }
        UserEntity saved = userRepository.save(toEntityUser(u));
        u.setId(saved.getId());
        UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.ADD, u);
        notifyObservers(event);
        return u;
    }

    @Transactional
    public User removeUser(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return null;
        }

        friendshipRepository.deleteAll(friendshipRepository.findAll().stream()
                .filter(friendship -> Objects.equals(friendship.getUser1(), id) || Objects.equals(friendship.getUser2(), id))
                .toList());

        messageRepository.deleteAll(messageRepository.findAll().stream()
                .filter(message -> Objects.equals(message.getSenderId(), id) || Objects.equals(message.getReceiverId(), id))
                .toList());

        userRepository.deleteById(id);
        User deletedUser = toDomainUser(user.get());
        UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.DELETE, deletedUser);
        notifyObservers(event);
        return deletedUser;
    }

    @Transactional
    public void sendFriendRequest(Long user1, Long user2) {
        if (userRepository.findById(user1).isEmpty() || userRepository.findById(user2).isEmpty()) {
            throw new ServiceException("User does not exist");
        }

        if (user1.equals(user2)) {
            throw new ServiceException("ID's cannot be the same");
        }

        if (friendshipRepository.findAll().stream()
                .map(this::toDomainFriendship)
                .anyMatch(prietenie -> prietenie.contains(user1) && prietenie.contains(user2))) {
            throw new ServiceException("Friendship already exists!");
        }

        Friendship prietenie = new Friendship(user1, user2, LocalDateTime.now());
        friendshipRepository.save(toEntityFriendship(prietenie));
        notifyObservers(null);
    }

    public void acceptFriendRequest(Long user1, Long user2) {
        if (userRepository.findById(user1).isEmpty() || userRepository.findById(user2).isEmpty()) {
            throw new ServiceException("User does not exist");
        }

        if (user1.equals(user2)) {
            throw new ServiceException("ID's cannot be the same");
        }

        Friendship prietenie = friendshipRepository.findAll().stream()
                .map(this::toDomainFriendship)
                .filter(p -> p.contains(user1) && p.contains(user2) && p.isPending())
                .findFirst()
                .orElseThrow(() -> new ServiceException("Friendship does not exist!"));

        prietenie.setPending(false);
        friendshipRepository.save(toEntityFriendship(prietenie));
        notifyObservers(null);
    }

    public void declineFriendRequest(Long user1, Long user2) {
        if (userRepository.findById(user1).isEmpty() || userRepository.findById(user2).isEmpty()) {
            throw new ServiceException("User does not exist");
        }

        if (user1.equals(user2)) {
            throw new ServiceException("ID's cannot be the same");
        }

        Friendship prietenie = friendshipRepository.findAll().stream()
                .map(this::toDomainFriendship)
                .filter(p -> p.contains(user1) && p.contains(user2) && p.isPending())
                .findFirst()
                .orElseThrow(() -> new ServiceException("Friendship does not exist!"));

        friendshipRepository.deleteById(prietenie.getId());
        notifyObservers(null);
    }

    public User updateUser(User u) {
        Optional<UserEntity> user = userRepository.findById(u.getId());
        if (user.isEmpty()) {
            throw new ServiceException("User does not exist!");
        }

        List<UserEntity> sameEmailUsers = userRepository.findAll().stream()
                .filter(user1 -> user1.getEmail().equals(u.getEmail()) && !Objects.equals(user1.getId(), u.getId()))
                .toList();
        if (!sameEmailUsers.isEmpty()) {
            throw new ServiceException("Email already in use!");
        }

        userRepository.save(toEntityUser(u));
        UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.UPDATE, u, toDomainUser(user.get()));
        notifyObservers(event);
        return toDomainUser(user.get());
    }

    public void removeFriendship(Long user1, Long user2) {
        List<Friendship> prietenie = friendshipRepository.findAll().stream()
                .map(this::toDomainFriendship)
                .filter(p -> p.getId1().equals(user1) && p.getId2().equals(user2) || p.getId1().equals(user2) && p.getId2().equals(user1))
                .toList();
        if (prietenie.isEmpty()) {
            throw new ServiceException("Friendship does not exist!");
        }
        prietenie.forEach(p -> friendshipRepository.deleteById(p.getId()));
    }

    public boolean addMessage(Long idFrom, Long idTo, String message) {
        Optional<User> from = findOneUser(idFrom);
        Optional<User> to = findOneUser(idTo);
        if (from.isEmpty() || to.isEmpty()) {
            throw new ServiceException("One or both users were not found");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new ServiceException("Message cannot be empty");
        }

        Message msg = new Message(from.get(), Collections.singletonList(to.get()), message);
        MessageEntity savedMessage = messageRepository.save(toEntityMessage(msg));
        msg.setId(savedMessage.getId());
        return true;
    }

    public ArrayList<Message> getMessages(Long id1, Long id2) {
        List<Message> messages = messageRepository.findAll().stream().map(this::toDomainMessage).toList();
        return messages.stream()
                .filter(msg -> ((msg.getSender().getId().equals(id1)) && msg.getReceivers().get(0).getId().equals(id2))
                        || (msg.getSender().getId().equals(id2) && msg.getReceivers().get(0).getId().equals(id1)))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Iterable<Friendship> getAllFriendships() {
        return friendshipRepository.findAll().stream().map(this::toDomainFriendship).toList();
    }

    public boolean isFriendship(Long id, Long id1) {
        return friendshipRepository.findAll().stream()
                .map(this::toDomainFriendship)
                .anyMatch(friendship -> friendship.contains(id) && friendship.contains(id1) && !friendship.isPending());
    }

    public void removeFriendships(Long id) {
        friendshipRepository.findAll().stream()
                .map(this::toDomainFriendship)
                .filter(friendship -> friendship.contains(id))
                .map(Friendship::getId)
                .forEach(friendshipRepository::deleteById);
    }

    public void removeMessages(Long id) {
        List<Message> messages = messageRepository.findAll().stream().map(this::toDomainMessage).toList();
        for (Message message : messages) {
            if (message.getSender().getId().equals(id) || message.getReceivers().get(0).getId().equals(id)) {
                messageRepository.deleteById(message.getId());
            }
        }
    }

    private User toDomainUser(UserEntity entity) {
        User user = new User(entity.getFirstName(), entity.getLastName(), entity.getEmail(), entity.getPassword(), entity.getProfilePicture());
        user.setId(entity.getId());
        return user;
    }

    private UserEntity toEntityUser(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setProfilePicture(user.getProfilePicture() == null ? new byte[0] : user.getProfilePicture());
        return entity;
    }

    private Friendship toDomainFriendship(FriendshipEntity entity) {
        Friendship friendship = new Friendship(entity.getUser1(), entity.getUser2(), entity.getFriendsSince());
        friendship.setId(entity.getId());
        friendship.setPending(entity.isPending());
        return friendship;
    }

    private FriendshipEntity toEntityFriendship(Friendship friendship) {
        FriendshipEntity entity = new FriendshipEntity();
        entity.setId(friendship.getId());
        entity.setUser1(friendship.getId1());
        entity.setUser2(friendship.getId2());
        entity.setFriendsSince(friendship.getDate());
        entity.setPending(friendship.isPending());
        return entity;
    }

    private Message toDomainMessage(MessageEntity entity) {
        User sender = findOneUser(entity.getSenderId()).orElseThrow(() -> new ServiceException("Sender not found"));
        User receiver = findOneUser(entity.getReceiverId()).orElseThrow(() -> new ServiceException("Receiver not found"));
        Message message = new Message(sender, Collections.singletonList(receiver), entity.getMessage(), entity.getDate());
        message.setId(entity.getId());
        return message;
    }

    private MessageEntity toEntityMessage(Message message) {
        MessageEntity entity = new MessageEntity();
        entity.setId(message.getId());
        entity.setSenderId(message.getSender().getId());
        entity.setReceiverId(message.getReceivers().get(0).getId());
        entity.setDate(message.getDate());
        entity.setMessage(message.getText());
        entity.setReplyId(message.getReply() == null ? null : message.getReply().getId());
        return entity;
    }
}

