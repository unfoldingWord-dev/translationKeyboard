/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */

/** Model class for an entity's property: a Java property mapped to a data base column. */
package tkdaogenerator;

public class Property {


    public final Schema schema;
    public final Entity entity;
    public PropertyType propertyType;
    public final String propertyName;

    public String columnName;
    public String columnType;

    public boolean primaryKey;
    public boolean pkAsc;
    public boolean pkDesc;
    public boolean pkAutoincrement;

    public boolean unique;
    public boolean notNull;

    /** Initialized in 2nd pass */
    public String constraints;

    public int ordinal;

    public String javaType;

    public Property(Schema schema, Entity entity, PropertyType propertyType, String propertyName) {
        this.schema = schema;
        this.entity = entity;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isAutoincrement() {
        return pkAutoincrement;
    }

    public String getConstraints() {
        return constraints;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public String getJavaType() {
        return javaType;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public Entity getEntity() {
        return entity;
    }

    void init2ndPass() {
        initConstraint();
        if (columnType == null) {
            columnType = schema.mapToDbType(propertyType);
        }
        if (columnName == null) {
            columnName = DaoUtil.dbName(propertyName);
        }
        if (notNull) {
            javaType = schema.mapToJavaTypeNotNull(propertyType);
        } else {
            javaType = schema.mapToJavaTypeNullable(propertyType);
        }
    }

    private void initConstraint() {
        StringBuilder constraintBuilder = new StringBuilder();
        if (primaryKey) {
            constraintBuilder.append("PRIMARY KEY");
            if (pkAsc) {
                constraintBuilder.append(" ASC");
            }
            if (pkDesc) {
                constraintBuilder.append(" DESC");
            }
            if (pkAutoincrement) {
                constraintBuilder.append(" AUTOINCREMENT");
            }
        }
        // Always have String PKs NOT NULL because SQLite is pretty strange in this respect:
        // One could insert multiple rows with NULL PKs
        if (notNull || (primaryKey && propertyType == PropertyType.String)) {
            constraintBuilder.append(" NOT NULL");
        }
        if (unique) {
            constraintBuilder.append(" UNIQUE");
        }
        String newContraints = constraintBuilder.toString().trim();
        if (constraintBuilder.length() > 0) {
            constraints = newContraints;
        }
    }

    void init3ndPass() {
        // Nothing to do so far
    }

    @Override
    public String toString() {
        return "Property " + propertyName + " of " + entity.getClassName();
    }

}
