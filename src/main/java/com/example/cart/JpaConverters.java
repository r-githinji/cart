package com.example.cart;

import java.util.stream.Stream;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public abstract class JpaConverters {
	
	@Converter(autoApply = true)
	public static class AccountTypeConverter implements AttributeConverter<AccountType, String> {
		
		@Override
		public String convertToDatabaseColumn(AccountType type) {
			return type.getValue();
		}

		@Override
		public AccountType convertToEntityAttribute(String dbData) {
			return Stream.of(AccountType.values()).filter(a ->  a.getValue().contentEquals(dbData)).findFirst().orElse(null);
		} 
	}
	
	@Converter(autoApply = true)
	public static class EntryTypeConverter implements AttributeConverter<EntryType, String> {
		
		@Override
		public String convertToDatabaseColumn(EntryType type) {
			return type.getValue();
		}

		@Override
		public EntryType convertToEntityAttribute(String dbData) {
			return Stream.of(EntryType.values()).filter(a ->  a.getValue().contentEquals(dbData)).findFirst().orElse(null);
		} 
	}
	
	@Converter(autoApply = true)
	public static class JournalTypeConverter implements AttributeConverter<JournalType, String> {
		
		@Override
		public String convertToDatabaseColumn(JournalType type) {
			return type.getValue();
		}

		@Override
		public JournalType convertToEntityAttribute(String dbData) {
			return Stream.of(JournalType.values()).filter(a ->  a.getValue().contentEquals(dbData)).findFirst().orElse(null);
		} 
	}
	
	@Converter(autoApply = true)
	public static class JournalSubtypeConverter implements AttributeConverter<JournalSubtype, String> {
		
		@Override
		public String convertToDatabaseColumn(JournalSubtype type) {
			return type.getValue();
		}

		@Override
		public JournalSubtype convertToEntityAttribute(String dbData) {
			return Stream.of(JournalSubtype.values()).filter(a ->  a.getValue().contentEquals(dbData)).findFirst().orElse(null);
		} 
	}
	
	@Converter(autoApply = true)
	public static class SaleStatusConverter implements AttributeConverter<SaleStatus, String> {
		
		@Override
		public String convertToDatabaseColumn(SaleStatus type) {
			return type.getValue();
		}

		@Override
		public SaleStatus convertToEntityAttribute(String dbData) {
			return Stream.of(SaleStatus.values()).filter(a ->  a.getValue().contentEquals(dbData)).findFirst().orElse(null);
		} 
	}
	
	@Converter(autoApply = true)
	public static class SaleTypeConverter implements AttributeConverter<SaleType, String> {
		
		@Override
		public String convertToDatabaseColumn(SaleType type) {
			return type.getValue();
		}

		@Override
		public SaleType convertToEntityAttribute(String dbData) {
			return Stream.of(SaleType.values()).filter(a ->  a.getValue().contentEquals(dbData)).findFirst().orElse(null);
		} 
	}
	
	@Converter(autoApply = true)
	public static class GenderTypeConverter implements AttributeConverter<GenderType, String> {
		
		@Override
		public String convertToDatabaseColumn(GenderType type) {
			return type.getValue();
		}

		@Override
		public GenderType convertToEntityAttribute(String dbData) {
			return Stream.of(GenderType.values()).filter(a ->  a.getValue().contentEquals(dbData)).findFirst().orElse(null);
		} 
	}
	
	@Converter(autoApply = true)
	public static class ItemStateConverter implements AttributeConverter<ItemState, String> {
		
		@Override
		public String convertToDatabaseColumn(ItemState type) {
			return type.getValue();
		}

		@Override
		public ItemState convertToEntityAttribute(String dbData) {
			return Stream.of(ItemState.values()).filter(a ->  a.getValue().contentEquals(dbData)).findFirst().orElse(null);
		} 
	}
}
