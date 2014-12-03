# app/views/api/v1/keyboard/index.json.rabl

object false

child @keyboards, :object_root => false do
  attributes :id, :updated_at, :iso_language, :iso_region, :language_name
end
