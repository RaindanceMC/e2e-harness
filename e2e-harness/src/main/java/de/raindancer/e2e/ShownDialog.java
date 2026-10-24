package de.raindancer.e2e;

import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A dialog window the server opened on a bot, read the way a player reads it: its title, the text of its body,
 * its input boxes and its buttons — each with what pressing it would send.
 *
 * @param texts  the plain text of every body element, in order (an item body's description included)
 * @param inputs the keys of its input boxes
 */
public record ShownDialog(String type, String title, List<String> texts, List<String> inputs, List<Button> buttons,
                          NbtMap raw) {

    /** A button: its label, and the action under it (null for one that only closes the window). */
    public record Button(String label, String tooltip, NbtMap action) {
    }

    /** Everything the body says, joined — what a scenario checks a sentence against. */
    public String text() {
        return String.join("\n", texts);
    }

    /** The first button whose label contains {@code label}. */
    public Optional<Button> button(String label) {
        return buttons.stream().filter(button -> button.label().contains(label)).findFirst();
    }

    static ShownDialog of(NbtMap dialog) {
        List<String> texts = new ArrayList<>();
        for (NbtMap body : maps(dialog.get("body"))) {
            if (body.containsKey("contents")) {
                texts.add(plain(body.get("contents")));
            }
            Object description = body.get("description");
            if (description instanceof NbtMap described) {
                texts.add(plain(described.containsKey("contents") ? described.get("contents") : described));
            } else if (description != null) {
                texts.add(plain(description));
            }
        }
        List<String> inputs = new ArrayList<>();
        for (NbtMap input : maps(dialog.get("inputs"))) {
            inputs.add(input.getString("key"));
        }
        List<Button> buttons = new ArrayList<>();
        for (NbtMap action : maps(dialog.get("actions"))) {
            buttons.add(button(action));
        }
        Object exit = dialog.get("exit_action");
        if (exit instanceof NbtMap exitAction) {
            buttons.add(button(exitAction));
        }
        Object yes = dialog.get("yes");
        if (yes instanceof NbtMap yesButton) {
            buttons.add(button(yesButton));
        }
        Object no = dialog.get("no");
        if (no instanceof NbtMap noButton) {
            buttons.add(button(noButton));
        }
        return new ShownDialog(dialog.getString("type"), plain(dialog.get("title")), List.copyOf(texts),
                List.copyOf(inputs), List.copyOf(buttons), dialog);
    }

    private static Button button(NbtMap button) {
        Object action = button.get("action");
        return new Button(plain(button.get("label")), button.containsKey("tooltip") ? plain(button.get("tooltip")) : "",
                action instanceof NbtMap map ? map : null);
    }

    private static List<NbtMap> maps(Object value) {
        List<NbtMap> found = new ArrayList<>();
        if (value instanceof NbtList<?> list) {
            for (Object each : list) {
                if (each instanceof NbtMap map) {
                    found.add(map);
                }
            }
        } else if (value instanceof NbtMap single) {
            found.add(single);
        }
        return found;
    }

    /** The text of a component as the network writes it: a string, a compound with text and extra, or a list. */
    static String plain(Object component) {
        if (component == null) {
            return "";
        }
        if (component instanceof String text) {
            return text;
        }
        StringBuilder out = new StringBuilder();
        if (component instanceof NbtList<?> list) {
            list.forEach(part -> out.append(plain(part)));
            return out.toString();
        }
        if (component instanceof NbtMap map) {
            if (map.containsKey("text")) {
                out.append(map.getString("text"));
            } else if (map.containsKey("")) {
                out.append(plain(map.get("")));
            } else if (map.containsKey("translate")) {
                out.append(map.getString("translate"));
            }
            if (map.containsKey("extra")) {
                out.append(plain(map.get("extra")));
            }
        }
        return out.toString();
    }
}
