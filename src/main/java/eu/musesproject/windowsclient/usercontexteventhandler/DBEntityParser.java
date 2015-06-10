package eu.musesproject.windowsclient.usercontexteventhandler;

/*
 * #%L
 * musesclient
 * %%
 * Copyright (C) 2013 - 2014 HITEC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import eu.musesproject.client.model.decisiontable.Action;
import eu.musesproject.windowsclient.model.ActionProperty;
import eu.musesproject.windowsclient.model.ContextEvent;
import eu.musesproject.windowsclient.model.Property;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by christophstanik on 4/22/14.
 */
public class DBEntityParser {
    /**
     * Method to create an {@link ContextEvent} db entity object
     * @param oldEvent {@link eu.musesproject.contextmodel.ContextEvent}
     * @return {@link ContextEvent} db entity object
     */
    public static ContextEvent transformContextEvent(int actionId, eu.musesproject.contextmodel.ContextEvent oldEvent) {
        ContextEvent contextEvent = new ContextEvent();
        contextEvent.setId(0);
        contextEvent.setId(actionId);
        contextEvent.setType(oldEvent.getType());
        contextEvent.setTimestamp(new Timestamp(oldEvent.getTimestamp()));

        return contextEvent;
    }

    /**
     * Method to create a List of {@link Property}s based on the Map of Properties in
     * the {@link eu.musesproject.contextmodel.ContextEvent}
     * @param contextEventId long id of the related {@link ContextEvent} in the db
     * @param oldEvent {@link eu.musesproject.contextmodel.ContextEvent}
     * @return {@link List} that contains {@link Property}
     */
    public static List<Property> transformProperty(long contextEventId, eu.musesproject.contextmodel.ContextEvent oldEvent) {
        List<Property> properties = new ArrayList<Property>();

        for(Map.Entry<String, String> entry : oldEvent.getProperties().entrySet()) {
            Property property = new Property();
            property.setId(0);
            property.setContexteventId((int) contextEventId);
            property.setKey(entry.getKey());
            property.setValue(String.valueOf(entry.getValue()));

            properties.add(property);
        }

        return properties;
    }


    public static Map<String, String> transformActionPropertyToMap(List<ActionProperty> entityActionProperties) {
        Map<String, String> properties = new HashMap<String, String>();

        for(ActionProperty actionProperty : entityActionProperties) {
            properties.put(actionProperty.getKey(), actionProperty.getValue());
        }

        return properties;
    }

    /**
     * Method that transform an {@link eu.musesproject.windowsclient.model.Action} object from the database
     * to an {@link Action} object that can be send to the server
     * @param entityAction {@link eu.musesproject.windowsclient.model.Action} entity object from the database
     * @return {@link Action}
     */
    public static Action transformAction(eu.musesproject.windowsclient.model.Action entityAction) {
        Action action = new Action();
        action.setId(entityAction.getId());
        action.setActionType(entityAction.getActionType());
        action.setDescription(entityAction.getDescription());
        entityAction.setTimestamp(new Timestamp(action.getTimestamp()));

        return action;
    }

    /**
     * Method that transform an {@link Action} object to an
     * {@link eu.musesproject.windowsclient.model.Action} entity object for the database
     * @param action {@link Action}
     * @return {@link eu.musesproject.windowsclient.model.Action} entity object for the database
     */
    public static eu.musesproject.windowsclient.model.Action transformActionToEntityAction(Action action) {
        eu.musesproject.windowsclient.model.Action entityAction = new eu.musesproject.windowsclient.model.Action();
        entityAction.setId(action.getId());
        entityAction.setActionType(action.getActionType());
        entityAction.setDescription(action.getDescription());
        entityAction.setTimestamp(new Timestamp(action.getTimestamp()));

        return entityAction;
    }

    public static eu.musesproject.contextmodel.ContextEvent transformEntityContextEvent(ContextEvent entityContextEvent) {
        eu.musesproject.contextmodel.ContextEvent contextEvent = new eu.musesproject.contextmodel.ContextEvent();
        contextEvent.setId(entityContextEvent.getId());
        contextEvent.setTimestamp(entityContextEvent.getTimestamp().getTime());
        contextEvent.setType(entityContextEvent.getType());

        return contextEvent;
    }
}