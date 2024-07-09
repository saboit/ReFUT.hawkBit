/**
 * Copyright (c) 2020 Bosch.IO GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.common.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.hawkbit.ui.common.data.proxies.ProxyIdentifiableEntity;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyNamedEntity;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyTarget;
import org.eclipse.hawkbit.ui.common.grid.support.DeleteSupport;
import org.eclipse.hawkbit.ui.utils.StringHtmlEncoder;
import org.eclipse.hawkbit.ui.utils.SPUIStyleDefinitions;
import org.eclipse.hawkbit.ui.utils.UIMessageIdProvider;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;

import com.cronutils.utils.StringUtils;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Builder class for grid components
 */
public final class GridComponentBuilder {
    public static final double DEFAULT_MIN_WIDTH = 100D;

    public static final String CREATED_BY_ID = "createdBy";
    public static final String CREATED_DATE_ID = "createdDate";
    public static final String MODIFIED_BY_ID = "modifiedBy";
    public static final String MODIFIED_DATE_ID = "modifiedDate";

    public static final String CREATED_BY_PROPERTY_NAME = "createdBy";
    public static final String CREATED_AT_PROPERTY_NAME = "createdAt";
    public static final String LAST_MODIFIED_BY_PROPERTY_NAME = "lastModifiedBy";
    public static final String LAST_MODIFIED_AT_PROPERTY_NAME = "lastModifiedAt";
    public static final String VERSION_PROPERTY_NAME = "version";
    public static final String NAME_PROPERTY_NAME = "name";
    public static final String CONTROLLER_ID_PROPERTY_NAME = "controllerId";

    private GridComponentBuilder() {
    }

    /**
     * Create a {@link Button} with link optic
     *
     * @param idSuffix
     *            suffix to build the button ID
     * @param idPrefix
     *            prefix to build the button ID
     * @param caption
     *            button caption
     * @param enabled
     *            is button enabled
     * @param clickListener
     *            execute on button click (null for none)
     * @return the button
     */
    public static Button buildLink(final String idSuffix, final String idPrefix, final String caption,
            final boolean enabled, final ClickListener clickListener) {
        final Button link = new Button();
        final String id = new StringBuilder(idPrefix).append('.').append(idSuffix).toString();

        link.setCaption(caption);
        link.setEnabled(enabled);
        link.setId(id);
        link.addStyleName("borderless");
        link.addStyleName("small");
        link.addStyleName("on-focus-no-border");
        link.addStyleName("link");
        if (clickListener != null) {
            link.addClickListener(clickListener);
        }
        link.setVisible(!StringUtils.isEmpty(caption));
        return link;
    }

    /**
     * Create a {@link Button} with link optic
     *
     * @param entity
     *            to build the button ID
     * @param idPrefix
     *            prefix to build the button ID
     * @param caption
     *            button caption
     * @param enabled
     *            is button enabled
     * @param clickListener
     *            execute on button click (null for none)
     * @return the button
     */
    public static <E extends ProxyNamedEntity> Button buildLink(final E entity, final String idPrefix,
            final String caption, final boolean enabled, final ClickListener clickListener) {
        final String idSuffix = StringHtmlEncoder.encode(entity.getName());
        return buildLink(idSuffix, idPrefix, caption, enabled, clickListener);
    }

    /**
     * Add name column to grid. The column is set to sortable which implies a
     * JPA field "name" that is used for sorting.
     *
     * @param <E>
     *            entity type of the grid
     * @param grid
     *            to add the column to
     * @param i18n
     *            message source for internationalization
     * @param columnId
     *            column ID
     * @return the created column
     */
    public static <E extends ProxyNamedEntity> Column<E, String> addNameColumn(final Grid<E> grid,
            final VaadinMessageSource i18n, final String columnId) {
        final Column<E, String> nameColumn = addColumn(i18n, grid, E::getName, "header.name", columnId,
                DEFAULT_MIN_WIDTH);
        setColumnSortable(nameColumn, NAME_PROPERTY_NAME);
        return nameColumn;
    }

    /**
     * Add controllerId column to grid.  The column is set to sortable which implies a
     * JPA field "controllerId" that is used for sorting.
     *
     * @param grid
     *            to add the column to
     * @param i18n
     *            message source for internationalization
     * @param columnId
     *            column ID
     * @return the created column
     */
    public static Column<ProxyTarget, Button> addControllerIdColumn(final Grid<ProxyTarget> grid,
            final VaadinMessageSource i18n, final String columnId) {
        final Column<ProxyTarget, Button> column = addComponentColumn(grid,
                t -> buildControllerIdLink(t, columnId)).setId(columnId)
                        .setCaption(i18n.getMessage("header.controllerId")).setHidable(false)
                        .setMinimumWidth(DEFAULT_MIN_WIDTH);
        setColumnSortable(column, CONTROLLER_ID_PROPERTY_NAME);
        return column;
    }

    private static Button buildControllerIdLink(final ProxyTarget target, final String linkIdPrefix) {
        final String idSuffix = StringHtmlEncoder.encode(target.getControllerId());
        return buildLink(idSuffix, linkIdPrefix, target.getControllerId(), true, clickEvent -> UI.getCurrent()
                .getNavigator().navigateTo("deployment/target=" + target.getControllerId()));
    }

    /**
     * Add description column to grid
     *
     * @param <E>
     *            entity type of the grid
     * @param grid
     *            to add the column to
     * @param i18n
     *            message source for internationalization
     * @param columnId
     *            column ID
     * @return the created column
     */
    public static <E extends ProxyNamedEntity> Column<E, String> addDescriptionColumn(final Grid<E> grid,
            final VaadinMessageSource i18n, final String columnId) {
        return addColumn(i18n, grid, E::getDescription, "header.description", columnId, DEFAULT_MIN_WIDTH);
    }

    /**
     * Add "created by", "created at", "modified by" and "modified at" column
     *
     * @param <E>
     *            entity type of the grid
     * @param grid
     *            to add the columns to
     * @param i18n
     *            message source for internationalization
     * @return the created columns
     */
    public static <E extends ProxyNamedEntity> List<Column<E, String>> addCreatedAndModifiedColumns(final Grid<E> grid,
            final VaadinMessageSource i18n) {
        final List<Column<E, String>> columns = new ArrayList<>();

        final Column<E, String> createdByColumn = addColumn(i18n, grid, E::getCreatedBy, "header.createdBy",
                CREATED_BY_ID, DEFAULT_MIN_WIDTH);
        setColumnSortable(createdByColumn, CREATED_BY_PROPERTY_NAME);
        columns.add(createdByColumn);

        final Column<E, String> createdDate = addColumn(i18n, grid, E::getCreatedDate, "header.createdDate", CREATED_DATE_ID, DEFAULT_MIN_WIDTH);
        setColumnSortable(createdDate, CREATED_AT_PROPERTY_NAME);
        columns.add(createdDate);

        final Column<E, String> modifiedBy = addColumn(i18n, grid, E::getLastModifiedBy, "header.modifiedBy", MODIFIED_BY_ID, DEFAULT_MIN_WIDTH);
        setColumnSortable(modifiedBy, LAST_MODIFIED_BY_PROPERTY_NAME);
        columns.add(modifiedBy);

        final Column<E, String> modifiedDate = addColumn(i18n, grid, E::getModifiedDate, "header.modifiedDate", MODIFIED_DATE_ID, DEFAULT_MIN_WIDTH);
        setColumnSortable(modifiedDate, LAST_MODIFIED_AT_PROPERTY_NAME);
        columns.add(modifiedDate);

        return columns;
    }

    /**
     * Add version column to grid
     *
     * @param <E>
     *            entity type of the grid
     * @param grid
     *            to add the column to
     * @param i18n
     *            message source for internationalization
     * @param valueProvider
     *            to get the version of the entity
     * @param columnId
     *            column ID
     * @return the created column
     */
    public static <E> Column<E, String> addVersionColumn(final Grid<E> grid, final VaadinMessageSource i18n,
            final ValueProvider<E, String> valueProvider, final String columnId) {
        final Column<E, String> column = addColumn(i18n, grid, valueProvider, "header.version", columnId,
                DEFAULT_MIN_WIDTH);
        setColumnSortable(column, VERSION_PROPERTY_NAME);
        return column;
    }

    private static <E, T> Column<E, T> addColumn(final VaadinMessageSource i18n, final Grid<E> grid,
            final ValueProvider<E, T> valueProvider, final String caption, final String columnID,
            final double minWidth) {
        final Column<E, T> col = addColumn(grid, valueProvider).setCaption(i18n.getMessage(caption))
                .setMinimumWidth(minWidth);
        if (columnID != null) {
            col.setId(columnID);
        }
        return col;

    }

    /**
     * Add column to grid with the standard settings
     *
     * @param <E>
     *            entity type of the grid
     * @param grid
     *            to add the column to
     * @param valueProvider
     *            providing the content
     * @return the created column
     */
    public static <E, T> Column<E, T> addColumn(final Grid<E> grid, final ValueProvider<E, T> valueProvider) {
        return addColumn(grid, valueProvider, null);
    }

    /**
     * Add column to grid with the standard settings
     *
     * @param <E>
     *            entity type of the grid
     * @param grid
     *            to add the column to
     * @param valueProvider
     *            providing the content
     * @param styleGenerator
     *            providing additional styles
     * @return the created column
     */
    public static <E, T> Column<E, T> addColumn(final Grid<E> grid, final ValueProvider<E, T> valueProvider,
            final StyleGenerator<E> styleGenerator) {
        final Column<E, T> column = grid.addColumn(valueProvider);
        commonColumnConfiguration(column, styleGenerator);
        return column;
    }

    /**
     * Remove column based on it's id
     * 
     * @param grid
     *            to add the column to
     * @param columnId
     *            of the column
     * @param <E>
     *            entity type of the grid
     */
    public static <E> void removeColumn(final Grid<E> grid, final String columnId) {
        grid.removeColumn(columnId);
    }

    /**
     * Check if a column is present based on it's id
     *
     * @param grid
     *            to add the column to
     * @param columnId
     *            of the column
     * @param <E>
     *            entity type of the grid
     * @return if the column is present
     */
    public static <E> boolean isColumnPresent(final Grid<E> grid, final String columnId) {
        return grid.getColumn(columnId) != null;
    }

    /**
     * Add column to grid with the standard settings
     *
     * @param <E>
     *            entity type of the grid
     * @param grid
     *            to add the column to
     * @param componentProvider
     *            providing the content
     * @return the created column
     */
    public static <E, T extends Component> Column<E, T> addComponentColumn(final Grid<E> grid,
            final ValueProvider<E, T> componentProvider) {
        return addComponentColumn(grid, componentProvider, null);
    }

    /**
     * Add column to grid with the standard settings
     *
     * @param <E>
     *            entity type of the grid
     * @param grid
     *            to add the column to
     * @param componentProvider
     *            providing the content
     * @param styleGenerator
     *            providing additional styles
     * @return the created column
     */
    public static <E, T extends Component> Column<E, T> addComponentColumn(final Grid<E> grid,
            final ValueProvider<E, T> componentProvider, final StyleGenerator<E> styleGenerator) {
        final Column<E, T> column = grid.addComponentColumn(componentProvider);
        commonColumnConfiguration(column, styleGenerator);
        return column;
    }

    private static <E, T> Column<E, T> commonColumnConfiguration(final Column<E, T> column,
            final StyleGenerator<E> styleGenerator) {
        column.setMinimumWidthFromContent(false);
        column.setExpandRatio(1);
        column.setSortable(false);

        if (styleGenerator != null) {
            column.setStyleGenerator(styleGenerator);
        }
        return column;
    }

    /**
     * Add delete button column to grid
     *
     * @param <E>
     *            entity type of the grid
     * @param grid
     *            to add the column to
     * @param i18n
     *            message source for internationalization
     * @param columnId
     *            column ID
     * @param deleteSupport
     *            that executes the deletion
     * @param buttonIdPrefix
     *            prefix to create the button IDs
     * @param buttonEnabled
     *            is the button enabled
     * @return the created column
     */
    public static <E extends ProxyIdentifiableEntity> Column<E, Button> addDeleteColumn(final Grid<E> grid,
            final VaadinMessageSource i18n, final String columnId, final DeleteSupport<E> deleteSupport,
            final String buttonIdPrefix, final Predicate<E> buttonEnabled) {
        final ValueProvider<E, Button> getDelButton = entity -> buildActionButton(i18n,
                clickEvent -> deleteSupport.openConfirmationWindowDeleteAction(entity), VaadinIcons.TRASH,
                UIMessageIdProvider.TOOLTIP_DELETE, SPUIStyleDefinitions.STATUS_ICON_NEUTRAL,
                buttonIdPrefix + "." + entity.getId(), buttonEnabled.test(entity));
        return addIconColumn(grid, getDelButton, columnId, i18n.getMessage("header.action.delete")).setWidth(60D)
                .setHidingToggleCaption(i18n.getMessage("header.action.delete"));
    }

    /**
     * Add an action button column to a grid
     *
     * @param <T>
     *            type of the entity displayed by the grid
     * @param grid
     *            to add the column to
     * @param iconProvider
     *            to get the icon from
     * @param columnId
     *            column ID
     * @param caption
     *            caption of the column
     * @return the created column
     */
    public static <T, V extends Component> Column<T, V> addIconColumn(final Grid<T> grid,
            final ValueProvider<T, V> iconProvider, final String columnId, final String caption) {
        return addIconColumn(grid, iconProvider, columnId, caption, null);
    }

    /**
     * Add an action button column to a grid
     *
     * @param <T>
     *            type of the entity displayed by the grid
     * @param grid
     *            to add the column to
     * @param iconProvider
     *            to get the icon from
     * @param columnId
     *            column ID
     * @param caption
     *            caption of the column
     * @param styleGenerator
     *            caption of the column
     * @return the created column
     */
    public static <T, V extends Component> Column<T, V> addIconColumn(final Grid<T> grid,
            final ValueProvider<T, V> iconProvider, final String columnId, final String caption,
            final StyleGenerator<T> styleGenerator) {
        final StyleGenerator<T> additionalStyleGenerator = entity -> SPUIStyleDefinitions.ICON_CELL;

        final StyleGenerator<T> finalStyleGenerator = merge(Arrays.asList(styleGenerator, additionalStyleGenerator));

        final Column<T, V> column = grid.addComponentColumn(iconProvider).setId(columnId)
                .setStyleGenerator(finalStyleGenerator).setWidth(60D).setResizable(false).setSortable(false);
        if (!StringUtils.isEmpty(caption)) {
            column.setCaption(caption);
        }
        return column;
    }

    /**
     * Makes the column sortable.
     *
     * @param <T>
     *            type of the entity displayed by the grid
     * @param <V>
     *            type of column value
     * @param column
     *            the column to set sortable
     * @param sortPropertyName
     *            the jpa property name to sort by
     * @return the provided column
     */
    public static <T, V> Column<T, V> setColumnSortable(final Column<T, V> column,
            final String sortPropertyName) {
        column.setSortable(true).setSortProperty(sortPropertyName);
        return column;
    }

    private static <T> StyleGenerator<T> merge(final Collection<StyleGenerator<T>> generators) {
        return item -> generators.stream().filter(Objects::nonNull).map(gen -> gen.apply(item)).filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    /**
     * Join columns to form an action column
     *
     * @param i18n
     *            message source for internationalization
     * @param headerRow
     *            header row
     * @param columns
     *            columns to join
     */
    public static void joinToActionColumn(final VaadinMessageSource i18n, final HeaderRow headerRow,
            final List<Column<?, ?>> columns) {
        joinToIconColumn(headerRow, i18n.getMessage("header.action"), columns);
    }

    /**
     * Join columns to form an icon column
     *
     * @param headerRow
     *            header row
     * @param headerCaption
     *            header caption
     * @param columns
     *            columns to join
     */
    public static void joinToIconColumn(final HeaderRow headerRow, final String headerCaption,
            final List<Column<?, ?>> columns) {
        columns.forEach(column -> {
            column.setWidth(30D);
            column.setResizable(false);
        });
        final Column<?, ?>[] columnArray = columns.toArray(new Column<?, ?>[columns.size()]);
        headerRow.join(columnArray).setText(headerCaption);
    }

    /**
     * Create an action button (e.g. a delete button)
     *
     * @param i18n
     *            message source for internationalization
     * @param clickListener
     *            clickListener
     * @param icon
     *            icon of the button
     * @param descriptionMsgProperty
     *            displayed as tool tip
     * @param style
     *            additional style
     * @param buttonId
     *            ID of the button
     * @param enabled
     *            is the button enabled
     * @return the button
     */
    public static Button buildActionButton(final VaadinMessageSource i18n, final ClickListener clickListener,
            final Resource icon, final String descriptionMsgProperty, final String style, final String buttonId,
            final boolean enabled) {
        final Button actionButton = new Button();

        actionButton.addClickListener(clickListener);
        actionButton.setIcon(icon, i18n.getMessage(descriptionMsgProperty));
        actionButton.setDescription(i18n.getMessage(descriptionMsgProperty));
        actionButton.setEnabled(enabled);
        actionButton.setId(buttonId);
        actionButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        actionButton.addStyleName("button-no-border");
        actionButton.addStyleName("action-type-padding");
        actionButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        actionButton.addStyleName(style);

        return actionButton;
    }
}
