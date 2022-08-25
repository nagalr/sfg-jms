package guru.springframework.sfgjms.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

/**
 * Created by jt on 2019-07-17.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage() throws JsonProcessingException {

        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello World!")
                .JMSCorrelationID(UUID.randomUUID().toString())
                .build();

        jmsTemplate. convertAndSend(JmsConfig.MY_QUEUE, message);
        log.debug(">>> Insert into MY_QUEUE message: " + message.toString());
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {

        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello")
                .build();

        Message receivedMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                Message helloMessage = null;

                try {
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_type", "guru.springframework.sfgjms.model.HelloWorldMessage");
                    helloMessage.setJMSCorrelationID(UUID.randomUUID().toString());

                    log.debug(">>> Insert into MY_SEND_RCV_QUEUE message: " + helloMessage.toString());

                    return helloMessage;

                } catch (JsonProcessingException e) {
                   throw new JMSException("boom");
                }
            }
        });

        log.debug(">>> Received Message: " + receivedMsg.getBody(String.class));

    }
}
