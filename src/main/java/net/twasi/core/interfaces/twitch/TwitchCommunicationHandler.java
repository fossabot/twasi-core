package net.twasi.core.interfaces.twitch;

import net.twasi.core.interfaces.api.CommunicationHandler;
import net.twasi.core.interfaces.twitch.TwitchInterface;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.models.Message.TwasiMessage;

import java.io.IOException;

public class TwitchCommunicationHandler extends CommunicationHandler {

    public TwitchCommunicationHandler(TwitchInterface inf) {
        super(inf);
    }

    @Override
        public boolean sendMessage(String message) {
            TwitchInterface twitchInterface = (TwitchInterface) getInterface();

            try {
                twitchInterface.getWriter().write("PRIVMSG " + getInterface().getStreamer().getUser().getTwitchAccount().getChannel() + " :" + message + "\n");
                twitchInterface.getWriter().flush();
                return true;
            } catch (IOException e) {
                TwasiLogger.log.error(e);
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public TwasiMessage readMessage() {
            TwitchInterface twitchInterface = (TwitchInterface) getInterface();

            try {
                String line = twitchInterface.getReader().readLine();
                if (line == null) {
                    return null;
                }
                return TwasiMessage.parse(line, getInterface());
            } catch (IOException e) {
                if (e.getMessage().equals("Socket closed")) {
                    TwasiLogger.log.info("Connection to Socket lost for Interface " + twitchInterface.getStreamer().getUser().getTwitchAccount().getUserName());
                    return null;
                }

                TwasiLogger.log.error(e);
                e.printStackTrace();
                return null;
            }
        }

}