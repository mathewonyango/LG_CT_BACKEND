package com.livinggoodsbackend.livinggoodsbackend.dto.chatDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessagePageDto {
    private List<MessageDto> messages;
    private PaginationDto pagination;
 


}
