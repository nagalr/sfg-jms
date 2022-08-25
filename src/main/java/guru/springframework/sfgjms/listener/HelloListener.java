package guru.springframework.sfgjms.listener;

import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

/**
 * Created by jt on 2019-07-17.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class HelloListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.MY_QUEUE)
    public void listen(@Payload HelloWorldMessage helloWorldMessage,
                       @Headers MessageHeaders headers, Message message){

        log.debug("<<< Within Listener to MY_QUEUE");
        log.debug("<<< Received in MY_QUEUE: " + helloWorldMessage);
    }

    @JmsListener(destination = JmsConfig.MY_SEND_RCV_QUEUE)
    public void listenAndResponse(@Payload HelloWorldMessage helloWorldMessage,
                                  @Headers MessageHeaders headers,
                                  Message message) throws JMSException {

        HelloWorldMessage payloadMsg = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Response Message!")
                .build();

        log.debug("<<< Within Listener to MY_SEND_RCV_QUEUE");
        log.debug("<<< Replay from MY_SEND_RCV_QUEUE: " + payloadMsg);

        jmsTemplate.convertAndSend(message.getJMSReplyTo(), payloadMsg);
    }
}
