package tc.oc.pgm.util.xml.parsers;

import org.bukkit.entity.LivingEntity;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.util.StringUtils;
import tc.oc.pgm.util.entity.EntityAttributes;
import tc.oc.pgm.util.entity.EntitySpecification;
import tc.oc.pgm.util.xml.InvalidXMLException;
import tc.oc.pgm.util.xml.Node;
import tc.oc.pgm.util.xml.XMLUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntityBuilder extends Builder<EntitySpecification, EntityBuilder> {
    private static final Set<String> RESERVED_ATTRIBUTES = Set.of("type");

    public EntityBuilder(@Nullable Element el, String... prop) {
        super(el, prop);
    }

    @Override
    protected EntitySpecification parse(Node node) throws InvalidXMLException {
        if (!node.isElement()) {
            return null;
        }
        final Attribute typeAttribute = XMLUtils.getRequiredAttribute(node.getElement(), "type");
        final Class<? extends LivingEntity> entityClass = getEntity(typeAttribute.getValue());
        if (entityClass == null) {
            throw new InvalidXMLException(
                String.format("Unknown entity type '%s'", typeAttribute.getValue()),
                node
            );
        }

        final List<EntitySpecification.AttributeApplication> attributeApplications = new ArrayList<>();
        for (final Attribute attribute : node.getElement().getAttributes()) {
            if (RESERVED_ATTRIBUTES.contains(attribute.getName())) {
                continue;
            }
            EntityAttributes.EntityAttributeDescription attributeData = EntityAttributes.INSTANCE.attributeData.get(
                StringUtils.simplify(attribute.getName())
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
            final Object attributeValue = attributeData.parser().apply(attribute.getValue());
            attributeApplications.add(
                new EntitySpecification.AttributeApplication(attributeValue, attributeData.function())
            );
        }

        return new EntitySpecification(entityClass, attributeApplications);
    }

    private Class<? extends LivingEntity> getEntity(final String entityName) {
        // todo: this is temporary, to be changed to lookup in entity registry
        try {
            return (Class<? extends LivingEntity>) Class.forName("org.bukkit.entity." + entityName);
        } catch (final ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    protected EntityBuilder getThis() {
        return this;
    }
}
