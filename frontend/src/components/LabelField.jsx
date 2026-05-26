import './LabelField.css';

const LabelInput = ({children, field}) => {
	return (
		<div className="label-field">
			<label className='childLabel'>{children}</label>
			<label className='fieldLabel'>{field}</label>
		</div>
	);
});

export default LabelInput;
