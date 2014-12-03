# app/views/api/v1/keyboard/index.json.rabl

object @a_keyboard
attributes :id => :keyboard_id, :name => :keyboard_name
attributes :created_at, :updated_at, :iso_region, :iso_language
child :keyboard_variants, :object_root => false do
  attributes :name, :created_at, :updated_at
  node :key_position_rows do |row|
    row.key_position_rows.map { |c|

        { :key_position_columns => partial("api/v1/keyboard/_key_pos", :object => c) }
    }

    #child row.each do |column|
      #{ :data => column }
    #  attributes :created_at
    #  child column.each do |key_position_itm|
          #attributes key_position_itm.created_at
    #      child key_position_itm.each do |a_character|
    #        attributes :modmask, :utf8hex
    #      end
    #  end
    #end
  end
end