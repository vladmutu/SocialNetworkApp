package org.example.socialnetwork.Service;

import org.example.socialnetwork.Domain.FriendRequest;
import org.example.socialnetwork.Domain.Friendship;
import org.example.socialnetwork.Domain.Message;
import org.example.socialnetwork.Domain.User;
import org.example.socialnetwork.Repository.PagingRepository;
import org.example.socialnetwork.Repository.Repository;
import org.example.socialnetwork.Utils.Events.ChangeEventType;
import org.example.socialnetwork.Utils.Events.UserEntityChangeEvent;
import org.example.socialnetwork.Utils.Observer.Observable;
import org.example.socialnetwork.Utils.Observer.Observer;
import org.example.socialnetwork.Utils.Pageable.Page;
import org.example.socialnetwork.Utils.Pageable.Pageable;
import org.example.socialnetwork.Validators.ServiceException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service implements Observable<UserEntityChangeEvent> {
    private final Repository<Long, User> userRepository;
    private final PagingRepository<Long, Friendship> friendshipRepository;
    private final List<Observer<UserEntityChangeEvent>> observers = new ArrayList<>();

    private final Repository<Long, Message> messageRepository;

    public Service(Repository<Long, User> userRepository, PagingRepository<Long, Friendship> friendshipRepository, Repository<Long, Message> messageRepository) {
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
        return StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(friendship -> friendship.getId2().equals(id) && friendship.isPending())
                .map(friendship -> {
                    User user = userRepository.findOne(friendship.getId1()).orElse(null);
                    assert user != null;
                    return new FriendRequest(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), friendship.getDate());
                })
                .collect(Collectors.toList());
    }

    public List<FriendRequest> getUsersFriendsDTO(Long id) {
        return StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.contains(id) && !prietenie.isPending())
                .map(prietenie -> {
                    Long friendId = prietenie.getId1().equals(id) ? prietenie.getId2() : prietenie.getId1();
                    User friend = userRepository.findOne(friendId).orElse(null);
                    if (friend != null) {
                        return new FriendRequest(prietenie.getId(), friend.getFirstName(), friend.getLastName(), friend.getEmail(), prietenie.getDate());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Page<Friendship> getUsersFriendsDTOOnPage(Pageable pageable, Long id) {
        return friendshipRepository.findAllOnPage(pageable, id);
    }

    public List<User> getUsersFriends(Long id) {
        return StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(prietenie -> prietenie.contains(id) && !prietenie.isPending())
                .map(prietenie -> {
                    Long friendId;
                    if (prietenie.getId1().equals(id)) {
                        friendId = prietenie.getId2();
                    } else {
                        friendId = prietenie.getId1();
                    }
                    return userRepository.findOne(friendId).orElse(null);
                })
                .collect(Collectors.toList());
    }

    public User findOneUserByNameAndEmail(String firstName, String lastName, String email) {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(user -> user.getFirstName().equals(firstName) && user.getLastName().equals(lastName) && user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findOneUser(Long id) {
        return userRepository.findOne(id);
    }

    public Long getNewUserId() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    public User addUser(User u) {
        List<User> sameEmailUsers = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(user -> user.getEmail().equals(u.getEmail()))
                .toList();
        if(!sameEmailUsers.isEmpty()) {
            throw new RuntimeException("Email already in use!");
        }
        else {
            u.setId(getNewUserId());
            if(userRepository.save(u).isEmpty()) {
                UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.ADD, u);
                notifyObservers(event);
            }
            return u;
        }
    }

    public User removeUser(Long id) {
        List<Long> friendshipsToRemove = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(friendship -> friendship.contains(id))
                .map(Friendship::getId)
                .toList();
        friendshipsToRemove.forEach(friendshipRepository::delete);
        Optional<User> user = userRepository.delete(id);
        if(user.isEmpty()) {
            UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.DELETE, user.get());
            notifyObservers(event);
        }
        return user.orElse(null);
    }

    public Long getNewFriendshipId() {
        return StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .map(Friendship::getId)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    public void sendFriendRequest(Long user1, Long user2) {
        if (userRepository.findOne(user1).isEmpty() || userRepository.findOne(user2).isEmpty()) {
            throw new ServiceException("User does not exist");
        }

        if (user1.equals(user2)) {
            throw new ServiceException("ID's cannot be the same");
        }

        if (StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .anyMatch(prietenie -> prietenie.contains(user1) && prietenie.contains(user2))) {
            throw new ServiceException("Friendship already exists!");
        }

        Friendship prietenie = new Friendship(user1,user2, LocalDateTime.now());
        prietenie.setId(getNewFriendshipId());
        friendshipRepository.save(prietenie);
        notifyObservers(null);
    }

    public void acceptFriendRequest(Long user1, Long user2) {
        if (userRepository.findOne(user1).isEmpty() || userRepository.findOne(user2).isEmpty()) {
            throw new ServiceException("User does not exist");
        }

        if (user1.equals(user2)) {
            throw new ServiceException("ID's cannot be the same");
        }

        Friendship prietenie = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(p -> p.contains(user1) && p.contains(user2) && p.isPending())
                .findFirst()
                .orElseThrow(() -> new ServiceException("Friendship does not exist!"));

        prietenie.setPending(false);
        friendshipRepository.update(prietenie);
        notifyObservers(null);
    }

    public void declineFriendRequest(Long user1, Long user2) {
        if (userRepository.findOne(user1).isEmpty() || userRepository.findOne(user2).isEmpty()) {
            throw new ServiceException("User does not exist");
        }

        if (user1.equals(user2)) {
            throw new ServiceException("ID's cannot be the same");
        }

        Friendship prietenie = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(p -> p.contains(user1) && p.contains(user2) && p.isPending())
                .findFirst()
                .orElseThrow(() -> new ServiceException("Friendship does not exist!"));

        friendshipRepository.delete(prietenie.getId());
        notifyObservers(null);
    }

    public User updateUser(User u) {
        Optional<User> user = userRepository.findOne(u.getId());
        if(user.isEmpty()) {
            throw new ServiceException("User does not exist!");
        }
        else {
            List<User> sameEmailUsers = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                    .filter(user1 -> user1.getEmail().equals(u.getEmail()))
                    .toList();
            if(sameEmailUsers.isEmpty()) {
                userRepository.update(u);
                UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.UPDATE, u, user.get());
                notifyObservers(event);
                return user.get();
            }
            else {
                throw new ServiceException("Email already in use!");
            }
        }
    }

    public void removeFriendship(Long user1, Long user2) {
        Iterable<Friendship> test = friendshipRepository.findAll();
        List<Friendship> prietenie = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(p -> p.getId1().equals(user1) && p.getId2().equals(user2) || p.getId1().equals(user2) && p.getId2().equals(user1))
                .toList();
        if(prietenie.isEmpty()) {
            throw new ServiceException("Friendship does not exist!");
        }
        prietenie.forEach(p -> friendshipRepository.delete(p.getId()));
    }

    public void addFrienship(Friendship friendship) {
        List<Friendship> friendships = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(f -> f.contains(friendship.getId1()) && f.contains(friendship.getId2()))
                .toList();
        if(!friendships.isEmpty()) {
           throw new ServiceException("Friendship already exists!");
        }
        if(friendship.getId1().equals(friendship.getId2())) {
            throw new ServiceException("ID's cannot be the same");
        }
        friendshipRepository.save(friendship);
    }

    public boolean addMessage(Long id_from, Long id_to, String message) {
        try {
            Optional<User> from = findOneUser(id_from);
            Optional<User> to = findOneUser(id_to);

            if (from.isPresent() && to.isPresent()) {
                if (message == null || message.trim().isEmpty()) {
                    throw new ServiceException("Mesajul nu poate fi gol");
                }

                Message msg = new Message(from.get(), Collections.singletonList(to.get()), message);
                messageRepository.save(msg);

                List<Message> messagesBetweenUsers = getMessages(id_to, id_from);
                if (messagesBetweenUsers.size() > 1) {
                    Message oldReplyMessage = messagesBetweenUsers.get(messagesBetweenUsers.size() - 2);
                    Message newReplyMessage = messagesBetweenUsers.get(messagesBetweenUsers.size() - 1);
                    oldReplyMessage.setReply(newReplyMessage);
                    messageRepository.update(oldReplyMessage);
                }
                else if (messagesBetweenUsers.size() == 1) {
                    Message newReplyMessage = messagesBetweenUsers.get(0);
                    newReplyMessage.setReply(newReplyMessage);
                    messageRepository.update(newReplyMessage);
                }

                return true;
            } else {
                throw new ServiceException("Unul sau ambii utilizatori nu au fost găsiți");
            }
        } catch (ServiceException se) {
            System.out.println("Eroare utilizator: " + se.getMessage());
        } catch (Exception ex) {
            System.out.println("Eroare creare mesaj: " + ex.getMessage());
        }
        return false;
    }

    public ArrayList<Message> getMessages(Long id1, Long id2) {
        Collection<Message> messages = (Collection<Message>) messageRepository.findAll();
        return messages.stream()
                .filter(msg -> ((msg.getSender().getId().equals(id1)) && msg.getReceivers().get(0).getId().equals(id2)) ||
                        (msg.getSender().getId().equals(id2) && msg.getReceivers().get(0).getId().equals(id1)))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Iterable<Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
    }

    public boolean isFriendship(Long id, Long id1) {
        return StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .anyMatch(friendship -> friendship.contains(id) && friendship.contains(id1) && !friendship.isPending());
    }

    public void removeFriendships(Long id) {
        StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(friendship -> friendship.contains(id))
                .map(Friendship::getId)
                .forEach(friendshipRepository::delete);
    }

    public void removeMessages(Long id) {
        Iterable<Message> messages = messageRepository.findAll();
        for(Message message : messages) {
            if(message.getSender().getId().equals(id) || message.getReceivers().get(0).getId().equals(id)) {
                messageRepository.delete(message.getId());
            }
        }
    }
}

