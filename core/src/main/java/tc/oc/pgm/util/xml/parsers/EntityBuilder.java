package tc.oc.pgm.util.xml.parsers;

import org.bukkit.entity.Entity;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jetbrains.annotations.Nullable;

import tc.oc.pgm.util.entity.EntityAttributeDescription;
import tc.oc.pgm.util.entity.EntityAttributes;
import tc.oc.pgm.util.entity.EntitySpecification;
import tc.oc.pgm.util.entity.ProxiedPhysicsEntitySpecification;
import tc.oc.pgm.util.entity.SimpleEntitySpecification;
import tc.oc.pgm.util.xml.InvalidXMLException;
import tc.oc.pgm.util.xml.Node;
import tc.oc.pgm.util.xml.XMLUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntityBuilder extends Builder<EntitySpecification, EntityBuilder> {
    private static final String TYPE_KEY = "type";
    private static final String KIND_KEY = "kind";
    private static final String SHOW_NAMETAG_KEY = "show-nametag";
    private static final String SIMPLE_KIND = "simple";
    private static final String COMPLEX_KIND = "complex";
    private static final Set<String> RESERVED_ATTRIBUTES = Set.of(TYPE_KEY, KIND_KEY, SHOW_NAMETAG_KEY);

    public EntityBuilder(@Nullable Element el, String... prop) {
        super(el, prop);
    }

    @Override
    protected EntitySpecification parse(Node node) throws InvalidXMLException {
        if (!node.isElement()) {
            return null;
        }
        String kindAttribute = XMLUtils.getNullableAttribute(
            node.getElement(), KIND_KEY
        );
        if (kindAttribute == null) {
            kindAttribute = SIMPLE_KIND;
        }
        var showNametag = XMLUtils.parseBoolean(node.getElement().getAttribute(SHOW_NAMETAG_KEY), true);
        return switch (kindAttribute.toLowerCase()) {
            case SIMPLE_KIND -> parseSimpleEntitySpecification(node, showNametag);
            case COMPLEX_KIND -> parseComplexSpecification(node, showNametag);
            default -> throw new InvalidXMLException(String.format("Kind '%s' is not known", kindAttribute), node);
        };
    }

    private SimpleEntitySpecification parseSimpleEntitySpecification(
        Node node, boolean showNametag
    ) throws InvalidXMLException {
        final Class<? extends Entity> entityClass = parseEntity(node);
        final List<SimpleEntitySpecification.AttributeApplication> attributeApplications = new ArrayList<>();
        applyDefaults(entityClass, attributeApplications);
        for (final Attribute attribute : node.getElement().getAttributes()) {
            if (RESERVED_ATTRIBUTES.contains(attribute.getName())) {
                continue;
            }
            EntityAttributeDescription attributeData = EntityAttributes.INSTANCE.attributeData.get(
                attribute.getName()
            );
            if (attributeData == null) {
                throw new InvalidXMLException(
                    String.format("Unknown entity attribute name '%s'", attribute.getName()),
                    node
                );
            }
            if (!attributeData.classRequired().isAssignableFrom(entityClass)) {
                throw new InvalidXMLException(
                    String.format(
                        "Attribute '%s' is only applicable for %s, which %s is not",
                        attribute.getName(),
                        attributeData.classRequired().getSimpleName(),
                        entityClass.getSimpleName()
                    ),
                    node
                );
            }
            try {
                final var attributeValue = attributeData.parser().apply(attribute.getValue());
                attributeApplications.add(
                    new SimpleEntitySpecification.AttributeApplication(attributeValue, attributeData.function())
                );
            } catch (RuntimeException e) {
                throw new InvalidXMLException(node, e);
            }
        }

        return new SimpleEntitySpecification(entityClass, attributeApplications, showNametag);
    }

    private static void applyDefaults(
        Class<? extends Entity> entityClass,
        List<SimpleEntitySpecification.AttributeApplication> attributeApplications
    ) {
        for (EntityAttributeDescription attributeData : EntityAttributes.INSTANCE.attributeData.values()) {
            if (!attributeData.classRequired().isAssignableFrom(entityClass) || attributeData.defaultValue() == null) {
                continue;
            }
            attributeApplications.add(
                new SimpleEntitySpecification.AttributeApplication(attributeData.defaultValue(), attributeData.function())
            );
        }
    }

    private ProxiedPhysicsEntitySpecification parseComplexSpecification(
        Node node, boolean showNametag
    ) throws InvalidXMLException {
        final SimpleEntitySpecification simpleEntitySpecification = parseSimpleEntitySpecification(node, showNametag);
        return new ProxiedPhysicsEntitySpecification(simpleEntitySpecification, 2);
    }

    private Class<? extends Entity> parseEntity(Node node) throws InvalidXMLException {
        final Attribute typeAttribute = XMLUtils.getRequiredAttribute(node.getElement(), TYPE_KEY);
        final Class<? extends Entity> entityClass = getEntity(typeAttribute.getValue());
        if (entityClass == null) {
            throw new InvalidXMLException(
                String.format("Unknown entity type '%s'", typeAttribute.getValue()),
                node
            );
        }
        return entityClass;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Entity> getEntity(final String entityName) {
        // todo: this is temporary, to be changed to lookup in entity registry
        try {
            return (Class<? extends Entity>) Class.forName("org.bukkit.entity." + entityName);
        } catch (final ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    protected EntityBuilder getThis() {
        return this;
    }
}
