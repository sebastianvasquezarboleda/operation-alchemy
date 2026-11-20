package com.flash.alchemy.domain.service;

import com.flash.alchemy.domain.exception.NotEnoughInformationException;

import java.util.ArrayList;
import java.util.List;


public class MessageDecoder {

    public String getMessage(List<List<String>> messages) {
        if (messages.isEmpty()) {
            throw new NotEnoughInformationException("There are no fragments to rebuild");
        }

        int maxLength = 0;
        for (List<String> message : messages) {
            if (message.size() > maxLength) {
                maxLength = message.size();
            }
        }

        List<String> words = new ArrayList<>();
        boolean foundFirstWord = false;

        for (int place = 0; place < maxLength; place++) {
            String word = "";

            for (List<String> message : messages) {
                // Align the fragments to the right (from the end)
                int offset = maxLength - message.size();
                int index = place - offset;
                if (index < 0) {
                    continue;
                }

                String currentWord = message.get(index).trim();
                if (currentWord.isEmpty()) {
                    continue;
                }

                if (word.isEmpty()) {
                    word = currentWord;
                } else if (!word.equals(currentWord)) {
                    throw new NotEnoughInformationException(
                            "The fragments have different words in the same place: '"
                                    + word + "' and '" + currentWord + "'");
                }
            }

            if (word.isEmpty()) {
                // Empty places at the beginning are the lag, we skip them.
                // An empty place after the first word means a missing word.
                if (foundFirstWord) {
                    throw new NotEnoughInformationException(
                            "The message is incomplete, a word is missing");
                }
            } else {
                foundFirstWord = true;
                words.add(word);
            }
        }

        if (words.isEmpty()) {
            throw new NotEnoughInformationException("All the fragments are empty");
        }

        return String.join(" ", words);
    }
}
