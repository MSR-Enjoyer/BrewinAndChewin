package umpaz.brewinandchewin.neoforge.registry;

import net.minecraft.core.Registry;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import umpaz.brewinandchewin.common.attachment.RagingAttachment;
import umpaz.brewinandchewin.common.attachment.TipsyHeartsAttachment;

public class BnCAttachments {
    public static final AttachmentType<RagingAttachment> RAGING = AttachmentType.builder(() -> new RagingAttachment(0, 0))
            .serialize(RagingAttachment.CODEC)
            .build();
    public static final AttachmentType<TipsyHeartsAttachment> TIPSY_HEARTS = AttachmentType.builder(() -> new TipsyHeartsAttachment(0, 0))
            .serialize(TipsyHeartsAttachment.CODEC)
            .build();

    public static void registerAll() {
        Registry.register(NeoForgeRegistries.ATTACHMENT_TYPES, RagingAttachment.ID, RAGING);
        Registry.register(NeoForgeRegistries.ATTACHMENT_TYPES, TipsyHeartsAttachment.ID, TIPSY_HEARTS);
    }
}
