package land.esta.plugin.ssb.utils.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Updates;
import land.esta.plugin.ssb.SmartBotsBackend;
import land.esta.plugin.ssb.bots.setting.Setting;
import land.esta.plugin.ssb.bots.setting.SettingManager;
import land.esta.plugin.ssb.bots.setting.SettingObject;
import land.esta.plugin.ssb.data.presets.PresetType;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MongoHandler {
    public static Mongo mongo() {
        return SmartBotsBackend.getInstance().getMongo();
    }

    public void createBotDoc(@NotNull UUID uuid) {
        Bson filter = Filters.eq("uuid", uuid.toString());
        MongoCollection<Document> players = mongo().getPlayers();
        Document playerDoc = players.find(filter).first();
        SettingManager settingManager = new SettingManager();

        if (playerDoc == null) {
            Document document = new Document();
            document.put("uuid", uuid.toString());
            document.put("botName", settingManager.getBotName());
            document.put("botSkin", settingManager.getSkin());
            document.put("settings", createBotSettings(settingManager, false));
            players.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
            return;
        }
        if (!playerDoc.containsKey("botName")) {
            playerDoc.put("botName", settingManager.getBotName());
        }
        if (!playerDoc.containsKey("botSkin")) {
            playerDoc.put("botSkin", settingManager.getSkin());
        }
        if (!playerDoc.containsKey("settings")) {
            playerDoc.put("settings", createBotSettings(settingManager, false));
            players.replaceOne(filter, playerDoc, new ReplaceOptions().upsert(true));
            return;
        }
        Document settingsDoc = (Document) playerDoc.get("settings");
        for (Setting setting : settingManager.getSettings()) {
            String databaseString = setting.toDatabaseString();
            if (!settingsDoc.containsKey(databaseString)) {
                playerDoc.put(databaseString, createBotSettingObjects(setting, false));
                continue;
            }
            Document settingObjectsDoc = (Document) settingsDoc.get(databaseString);
            for (SettingObject settingObject : setting.getSettings()) {
                String databaseStringObject = settingObject.toDatabaseString();
                if (settingObjectsDoc.containsKey(databaseStringObject)) continue;
                settingObjectsDoc.put(databaseStringObject, createBotSettingObject(settingObject, false));
            }
        }
        players.replaceOne(filter, playerDoc, new ReplaceOptions().upsert(true));
    }

    public void createBotDoc(String difficulty) {
        Document difficultyDoc = mongo().getDifficulties().find(Filters.eq("difficulty", difficulty)).first();
        SettingManager settingManager = new SettingManager();

        if (difficultyDoc == null) {
            Document document = new Document();
            document.put("difficulty", difficulty);
            document.put("botName", settingManager.getBotName());
            document.put("botSkin", settingManager.getSkin());
            document.put("settings", createBotSettings(settingManager, false));
            mongo().getDifficulties().replaceOne(Filters.eq("difficulty", difficulty), document, new ReplaceOptions().upsert(true));
        } else {
            if (!difficultyDoc.containsKey("botName")) {
                difficultyDoc.put("botName", settingManager.getBotName());
            }
            if (!difficultyDoc.containsKey("botSkin")) {
                difficultyDoc.put("botSkin", settingManager.getSkin());
            }
            if (!difficultyDoc.containsKey("settings")) {
                difficultyDoc.put("settings", createBotSettings(settingManager, false));
                mongo().getDifficulties().replaceOne(Filters.eq("difficulty", difficulty), difficultyDoc, new ReplaceOptions().upsert(true));
            } else {
                Document settingsDoc = (Document) difficultyDoc.get("settings");
                for (Setting setting : settingManager.getSettings()) {
                    if (!settingsDoc.containsKey(setting.toDatabaseString())) {
                        difficultyDoc.put(setting.toDatabaseString(), createBotSettingObjects(setting, false));
                    } else {
                        Document settingObjectsDoc = (Document) settingsDoc.get(setting.toDatabaseString());
                        for (SettingObject settingObject : setting.getSettings()) {
                            if (!settingObjectsDoc.containsKey(settingObject.toDatabaseString())) {
                                settingObjectsDoc.put(settingObject.toDatabaseString(), createBotSettingObject(settingObject, false));
                            }
                        }
                    }
                }
                mongo().getDifficulties().replaceOne(Filters.eq("difficulty", difficulty), difficultyDoc, new ReplaceOptions().upsert(true));
            }
        }
    }

    public void createBotDoc(PresetType presetType, String name) {
        Document presetDoc = getPresetCollection(presetType).find(Filters.eq("presetName", name)).first();
        SettingManager settingManager = new SettingManager();

        if (presetDoc == null) {
            Document document = new Document();
            document.put("presetName", name);
            document.put("botName", settingManager.getBotName());
            document.put("botSkin", settingManager.getSkin());
            switch (presetType) {
                case UNRANKED:
                    document.put("gameRandomizer", settingManager.isGameRandomizer());
                    break;
                case RANKED:
                    document.put("elo", settingManager.getElo());
                    break;
                case TOURNAMENT:
                    document.put("tier", settingManager.getTier());
                    break;
            }
            document.put("settings", presetType == PresetType.UNRANKED ? createBotSettings(settingManager, true) : createBotSettings(settingManager, false));
            getPresetCollection(presetType).replaceOne(Filters.eq("presetName", name), document, new ReplaceOptions().upsert(true));
        } else {
            if (!presetDoc.containsKey("botName")) {
                presetDoc.put("botName", settingManager.getBotName());
            }
            if (!presetDoc.containsKey("botSkin")) {
                presetDoc.put("botSkin", settingManager.getSkin());
            }
            if (presetType == PresetType.UNRANKED && !presetDoc.containsKey("gameRandomizer")) {
                presetDoc.put("gameRandomizer", settingManager.isGameRandomizer());
            }
            if (presetType == PresetType.RANKED && !presetDoc.containsKey("elo")) {
                presetDoc.put("elo", settingManager.getElo());
            }
            if (presetType == PresetType.TOURNAMENT && !presetDoc.containsKey("tier")) {
                presetDoc.put("tier", settingManager.getTier());
            }
            if (!presetDoc.containsKey("settings")) {
                presetDoc.put("settings", presetType == PresetType.UNRANKED ? createBotSettings(settingManager, true) : createBotSettings(settingManager, false));
                getPresetCollection(presetType).replaceOne(Filters.eq("presetName", name), presetDoc, new ReplaceOptions().upsert(true));
            } else {
                Document settingsDoc = (Document) presetDoc.get("settings");
                for (Setting setting : settingManager.getSettings()) {
                    if (!settingsDoc.containsKey(setting.toDatabaseString())) {
                        presetDoc.put(setting.toDatabaseString(), presetType == PresetType.UNRANKED ? createBotSettingObjects(setting, true) : createBotSettingObjects(setting, false));
                    } else {
                        Document settingObjectsDoc = (Document) settingsDoc.get(setting.toDatabaseString());
                        for (SettingObject settingObject : setting.getSettings()) {
                            if (!settingObjectsDoc.containsKey(settingObject.toDatabaseString())) {
                                settingObjectsDoc.put(settingObject.toDatabaseString(), presetType == PresetType.UNRANKED ? createBotSettingObject(settingObject, true) : createBotSettingObject(settingObject, false));
                            }
                        }
                    }
                }
                getPresetCollection(presetType).replaceOne(Filters.eq("presetName", name), presetDoc, new ReplaceOptions().upsert(true));
            }
        }
    }

    public Document createBotSettings(SettingManager settings, boolean unranked) {
        Document document = new Document();
        for (Setting setting : settings.getSettings()) {
            document.put(setting.toDatabaseString(), createBotSettingObjects(setting, unranked));
        }
        return document;
    }

    public Document createBotSettingObjects(Setting setting, boolean unranked) {
        Document settingCategoryDocument = new Document();
        for (SettingObject settingObject : setting.getSettings()) {
            settingCategoryDocument.put(settingObject.toDatabaseString(), createBotSettingObject(settingObject, unranked));
        }
        return settingCategoryDocument;
    }

    public Document createBotSettingObject(SettingObject settingObject, boolean unranked) {
        Document document = new Document();
        Object obj = settingObject.getObject();
        document.put("value", obj);
        if (obj instanceof Double || obj instanceof Integer) {
            document.put("randomizer", settingObject.getRandomizer());
            if (unranked) {
                document.put("min", Double.toString(settingObject.getMin()));
                document.put("max", Double.toString(settingObject.getMax()));
            }
        }
        return document;
    }

    public MongoCollection<Document> getPresetCollection(PresetType type) {
        return switch (type) {
            case UNRANKED -> mongo().getUnranked();
            case RANKED -> mongo().getRanked();
            case TOURNAMENT -> mongo().getTournament();
            default -> null;
        };
    }

    public void deletePreset(PresetType presetType, String name) {
        new DeletePresetRunnable(presetType, name).runTaskAsynchronously(SmartBotsBackend.getInstance());
    }

    public void updateValue(UUID uuid, String name, Object obj) {
        new UpdateValueRunnable(uuid, name, obj).runTaskAsynchronously(SmartBotsBackend.getInstance());
    }

    public void updateValue(String difficulty, String name, Object obj) {
        new BukkitRunnable() {
            @Override
            public void run() {
                mongo().getDifficulties().updateOne(Filters.eq("difficulty", difficulty), Updates.set(name, obj));
            }
        }.runTaskAsynchronously(SmartBotsBackend.getInstance());
    }

    public void updateValue(PresetType presetType, String presetName, String settingName, Object obj) {
        new BukkitRunnable() {
            @Override
            public void run() {
                getPresetCollection(presetType).updateOne(Filters.eq("presetName", presetName), Updates.set(settingName, obj));
            }
        }.runTaskAsynchronously(SmartBotsBackend.getInstance());
    }

    public Object findField(UUID uuid, String document) {
        Document playerDoc = mongo().getPlayers().find(Filters.eq("uuid", uuid.toString())).first();
        if (playerDoc != null) {
            return playerDoc.get(document);
        }
        return null;
    }

    public Object findField(String difficulty, String document) {
        Document playerDoc = mongo().getDifficulties().find(Filters.eq("difficulty", difficulty)).first();
        if (playerDoc != null) {
            return playerDoc.get(document);
        }
        return null;
    }

    public Object findField(PresetType presetType, String name, String document) {
        Document playerDoc = getPresetCollection(presetType).find(Filters.eq("presetName", name)).first();
        if (playerDoc != null) {
            return playerDoc.get(document);
        }
        return null;
    }

    private static class UpdateValueRunnable extends BukkitRunnable {
        private final UUID uuid;
        private final String name;
        private final Object obj;

        public UpdateValueRunnable(UUID uuid, String name, Object obj) {
            this.uuid = uuid;
            this.name = name;
            this.obj = obj;
        }

        @Override
        public void run() {
            //Bukkit.broadcastMessage("Name: " + name);
            mongo().getPlayers().updateOne(Filters.eq("uuid", uuid.toString()), Updates.set(name, obj));
        }
    }

    private class DeletePresetRunnable extends BukkitRunnable {
        private final PresetType presetType;
        private final String name;

        public DeletePresetRunnable(PresetType presetType, String name) {
            this.presetType = presetType;
            this.name = name;
        }

        @Override
        public void run() {
            getPresetCollection(presetType).deleteOne(Filters.eq("presetName", name));
        }
    }
}
